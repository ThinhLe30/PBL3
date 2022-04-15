//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.pbl3;

import java.io.*;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;

public class ProductController implements Initializable {
    @FXML
    Button butAdd;
    @FXML
    Button butReset;
    @FXML
    Button ButUpdate;
    @FXML
    Button ButDelete;
    @FXML
    Button butBrowse;
    @FXML
    Label ProductLabel;
    @FXML
    ComboBox<String> cbbCategory;
    @FXML
    TextField txtProductName;
    @FXML
    TextField txtPurchasePrice;
    @FXML
    TextField txtSalePrice;
    @FXML
    TextField txtBarcode;
    @FXML
    DatePicker txtDayPicker;
    @FXML
    AnchorPane AnchorPane;
    @FXML
    ImageView ProductImgView;
    @FXML
    TableView<Product> ProductTableView;
    @FXML
    TableColumn<Product, String> Col_Category;
    @FXML
    TableColumn<Product, String> Col_Name;
    @FXML
    TableColumn<Product, Float> Col_PurchasePrice;
    @FXML
    TableColumn<Product, Float> Col_SalePrice;
    @FXML
    TableColumn<Product, String> Col_Barcode;
    @FXML
    TableColumn<Product, Date> Col_importDate;
    @FXML
    private Button searchBT;
    @FXML
    private TextField searchTextField;
    @FXML
    private ComboBox<String> cbbprice;
    @FXML
    private RadioButton ppriceRadioBT;
    @FXML
    private RadioButton spriceRadioBT;
    @FXML
    private Button signOutButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button homepageButton;
    @FXML
    private Button accountButton;

    OpenUI openUI = new OpenUI();

    private File file;
    private FileChooser fileChooser;
    private Image image;
    private FileInputStream fileinputstream;
    private PreparedStatement store = null;
    private PreparedStatement retrive = null;
    private PreparedStatement update = null;
    private PreparedStatement delete = null;
    //OpenView openView = new OpenView();
    int min = 0, max = 0;
    String condition = "SalePrice";


    public ProductController() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.ppriceRadioBT.setSelected(true);
        DatabaseConnection connection = new DatabaseConnection();
        Connection link = connection.getConnection();
        try {
            String query = "select ProductImage from products where Barcode = (?)";
            this.retrive = link.prepareStatement(query, 1005, 1008);
            String query1 = "insert into products (Barcode,ProductName,SalePrice,PurchasePrice,ProductImage,ImportDay,Category) values (?,?,?,?,?,?,?)";
            this.store = link.prepareStatement(query1);
            String query2 = "update products set ProductName = ? , SalePrice = ?, PurchasePrice = ?, ImportDay = ?, Category = ?, ProductImage = ? where Barcode = ?";
            this.update = link.prepareStatement(query2);
            String query3 = "DELETE FROM products WHERE Barcode  = ? ";
            this.delete = link.prepareStatement(query3);
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

        this.CompletedCombobox();
        this.loadTable("");
        min = max = 0;
        this.butBrowse.setOnAction((e) -> {
            this.OpenImageBrowse();
        });
        this.butAdd.setOnAction((e) -> {
            if(!ValidationField())
            {
                this.AddProduct();
            }
            else
            {
                Notifications.create().text("Please fill in all fields.").title("Oh Snap!").hideAfter(Duration.seconds(5.0D)).action(new Action[0]).show();
            }

        });
        this.ButUpdate.setOnAction((e) -> {
            this.UpdateProduct();
        });
        this.butReset.setOnAction((e) -> {
            this.Clear();
        });
        this.ButDelete.setOnAction(e->{
            this.Delete();
        });
        this.ProductTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.SelectedRowAction();
            }

        });
    }

    public boolean ValidationField()
    {
        return((txtBarcode.getText().trim().isEmpty()) || (txtPurchasePrice.getText().trim().isEmpty()) || (txtSalePrice.getText().trim().isEmpty())
                || (txtProductName.getText().trim().isEmpty()) || (txtDayPicker.getValue() == null) ||
                (cbbCategory.getSelectionModel().getSelectedItem() == null) || (this.file == null));

    }

    public void SelectedRowAction() {
        if (((Product)this.ProductTableView.getSelectionModel().getSelectedItem()).getProductName() != "") {
            this.txtBarcode.setEditable(false);
            butAdd.setDisable(true);
            this.txtBarcode.setText(((Product)this.ProductTableView.getSelectionModel().getSelectedItem()).getBarcode());
            this.txtProductName.setText(((Product)this.ProductTableView.getSelectionModel().getSelectedItem()).getProductName());
            this.txtSalePrice.setText(Float.toString(((Product)this.ProductTableView.getSelectionModel().getSelectedItem()).getSalePrice()));
            this.txtPurchasePrice.setText(Float.toString(((Product)this.ProductTableView.getSelectionModel().getSelectedItem()).getPurchasePrice()));
            Date date = this.ProductTableView.getSelectionModel().getSelectedItem().getImportDay();
            this.txtDayPicker.setValue(LocalDate.of(date.getYear()+1900, date.getMonth()+1, date.getDate()));
            this.txtDayPicker.setShowWeekNumbers(true);
            this.cbbCategory.setValue(this.ProductTableView.getSelectionModel().getSelectedItem().getCategory());
            try {
                this.retrive.setString(1, ((Product)this.ProductTableView.getSelectionModel().getSelectedItem()).getBarcode());
                ResultSet result = this.retrive.executeQuery();
                if (result.first()) {
                    Blob blob = result.getBlob("ProductImage");
                    InputStream input = blob.getBinaryStream();
                    Image image = new Image(input);
                    this.ProductImgView.setImage(image);
                }
            } catch (SQLException var5) {
                var5.printStackTrace();
            }
        }

    }

    public void Clear() {
        ProductLabel.setText("Add Product");
        txtProductName.setText("");
        txtSalePrice.setText("");
        txtBarcode.setText("");
        txtBarcode.setEditable(true);
        txtPurchasePrice.setText("");
        ProductImgView.setImage(null);
        cbbCategory.getSelectionModel().clearSelection();
        txtDayPicker.getEditor().clear();
        ProductTableView.getSelectionModel().clearSelection();
        searchTextField.setText("");
        butAdd.setDisable(false);
        this.file = null;
    }

    public void DisableEditableField()
    {
        txtProductName.setEditable(false);
        txtDayPicker.setEditable(false);
        txtSalePrice.setEditable(false);
        txtPurchasePrice.setEditable(false);
        txtBarcode.setEditable(false);
        cbbCategory.setEditable(false);
        butAdd.setDisable(true);
        //ButDelete.setDisable(true);
        //ButUpdate.setDisable(true);
        butBrowse.setDisable(true);
    }
    public void EnableEditableField()
    {
        txtProductName.setEditable(true);
        txtDayPicker.setEditable(true);
        txtSalePrice.setEditable(true);
        txtPurchasePrice.setEditable(true);
        txtBarcode.setEditable(true);
        cbbCategory.setEditable(true);
        butAdd.setDisable(false);
        //ButDelete.setDisable(false);
        //ButUpdate.setDisable(false);
        butBrowse.setDisable(false);
    }

    public void AddProduct() {
        try {
            this.store.setString(1, this.txtBarcode.getText());
            this.store.setString(2, this.txtProductName.getText());
            this.store.setFloat(3, Float.parseFloat(this.txtSalePrice.getText()));
            this.store.setFloat(4, Float.parseFloat(this.txtPurchasePrice.getText()));
            this.fileinputstream = new FileInputStream(this.file);
            this.store.setBinaryStream(5, this.fileinputstream, (int)this.file.length());
            this.store.setDate(6, java.sql.Date.valueOf((LocalDate)this.txtDayPicker.getValue()));
            this.store.setString(7, ((String)this.cbbCategory.getSelectionModel().getSelectedItem()).toString());
            this.store.execute();
            Notifications.create().text("You have add product successfully into our system.").title("Well-done!").hideAfter(Duration.seconds(5.0D)).action(new Action[0]).show();
            Clear();
            this.loadTable("");
            min = max = 0;
        } catch (SQLException | FileNotFoundException var2) {
            Notifications.create().text("You have failed add product in to our System. Try again!").title("Oh Snap!").hideAfter(Duration.seconds(5.0D)).show();
            System.err.println(var2);
        }

    }

    public void CompletedCombobox() {
        this.cbbCategory.setItems(FXCollections.observableArrayList(new String[]{"Laptop", "Phone", "Computer"}));
        this.cbbprice.setItems(FXCollections.observableArrayList(new String[]{"0-10", "10-50", "50-100", "100-500","500-5000","5000 or more", "All"}));
    }

    public void loadTable(String txt) {
        ObservableList<Product> list = FXCollections.observableArrayList();
        DatabaseConnection ConnectNow = new DatabaseConnection();
        Connection connectDB = ConnectNow.getConnection();
        String accountQuery = "select Barcode,ProductName,SalePrice,PurchasePrice,ImportDay,Category from products" +
                " where (ProductName LIKE '%" + txt + "%' OR Category = '" + txt + "') AND (" + condition + " >= " + min;
        if(max != 0)
            accountQuery += " AND " + condition + " < " + max + ")";
        else accountQuery += ")";


        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(accountQuery);

            while(queryResult.next()) {
                String Barcode = queryResult.getString("Barcode");
                String ProductName = queryResult.getString("ProductName");
                Float SalePrice = queryResult.getFloat("SalePrice");
                Float PurchasePrice = queryResult.getFloat("PurchasePrice");
                Date ImportDay = queryResult.getDate("ImportDay");
                String Category = queryResult.getString("Category");
                Product product = new Product(Barcode, ProductName, SalePrice, PurchasePrice, ImportDay, Category);
                list.add(product);
            }

            this.Col_Barcode.setCellValueFactory(new PropertyValueFactory("Barcode"));
            this.Col_Name.setCellValueFactory(new PropertyValueFactory("ProductName"));
            this.Col_SalePrice.setCellValueFactory(new PropertyValueFactory("salePrice"));
            this.Col_PurchasePrice.setCellValueFactory(new PropertyValueFactory("purchasePrice"));
            this.Col_importDate.setCellValueFactory(new PropertyValueFactory("importDay"));
            this.Col_Category.setCellValueFactory(new PropertyValueFactory("Category"));
            this.ProductTableView.setItems(list);
        } catch (SQLException var14) {
            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, (String)null, var14);
            var14.printStackTrace();
        }

    }

    public void OpenImageBrowse() {
        this.fileChooser = new FileChooser();
        Stage stage = (Stage)this.AnchorPane.getScene().getWindow();
        this.file = this.fileChooser.showOpenDialog(stage);
        if (this.file != null) {
            this.image = new Image(this.file.toURI().toString(), 248.0D, 196.0D, true, true);
            this.ProductImgView.setImage(this.image);
            this.ProductImgView.fitHeightProperty();
            this.ProductImgView.fitWidthProperty();
            this.ProductImgView.setPreserveRatio(true);
        }

    }

    @FXML
    void searchOnAction(ActionEvent event) {
        String[] price = new String[2];
        if(this.cbbprice.getSelectionModel().getSelectedItem() != null)
        {
            if(this.cbbprice.getSelectionModel().getSelectedItem().contains("or more"))
            {
                min = 5000;
                max = 0;
            }
            else if(this.cbbprice.getSelectionModel().getSelectedItem().contains("All"))
            {
                min = max = 0;
            }
            else
            {
                price = this.cbbprice.getSelectionModel().getSelectedItem().split("-");
                min = Integer.parseInt(price[0]);
                max = Integer.parseInt(price[1]);
            }
        }
        if(ppriceRadioBT.isSelected()) condition = "PurchasePrice";
        loadTable(searchTextField.getText());
        min = max = 0;
    }
    @FXML
    private void UpdateProduct() {
        try {
            this.update.setString(1, this.txtProductName.getText());
            this.update.setFloat(2, Float.parseFloat(this.txtSalePrice.getText()));
            this.update.setFloat(3, Float.parseFloat(this.txtPurchasePrice.getText()));
            this.update.setDate(4, java.sql.Date.valueOf((LocalDate) this.txtDayPicker.getValue()));
            this.update.setString(5, ((String)this.cbbCategory.getSelectionModel().getSelectedItem()));
            if(file != null)
            {
                this.fileinputstream = new FileInputStream(this.file);
                this.update.setBinaryStream(6, this.fileinputstream, (int)this.file.length());
            }
            else
            {
                try {
                    this.retrive.setString(1, ((Product)this.ProductTableView.getSelectionModel().getSelectedItem()).getBarcode());
                    ResultSet result = this.retrive.executeQuery();
                    if (result.first()) {
                        Blob blob = result.getBlob("ProductImage");
                        this.update.setBinaryStream(6,blob.getBinaryStream());
                    }
                } catch (SQLException var5) {
                    var5.printStackTrace();
                }
            }
            this.update.setString(7, this.txtBarcode.getText());
            this.update.execute();

            Clear();
            this.loadTable("");
            Notifications.create().text("You have update product successfully into our system.").title("Well-done!").hideAfter(Duration.seconds(5.0D)).action(new Action[0]).show();
            min = max = 0;
        } catch (SQLException | FileNotFoundException var2) {
            Notifications.create().text("You have failed update account in to our System. Try again!").title("Oh Snap!").hideAfter(Duration.seconds(5.0D)).show();
            System.err.println(var2);
        }
    }

    public void Delete()
    {
        Product selected = ProductTableView.getSelectionModel().getSelectedItem();
        try {
            this.delete.setString(1,selected.getBarcode());
            delete.execute();
            Notifications.create().text("successfully .").title("Well-done!").hideAfter(Duration.seconds(5.0D)).action(new Action[0]).show();
            loadTable("");
            Clear();
        } catch (Exception var15) {
            var15.printStackTrace();
            Notifications.create().text("error!").title("Oh Snap!").hideAfter(Duration.seconds(5.0D)).show();
        }
    }

    @FXML
    public void signOutButtonOnAction(ActionEvent event) {
        Stage stage = (Stage)this.signOutButton.getScene().getWindow();
        stage.close();
        openUI.Open_UI("LoginUI.fxml","");
    }
    @FXML
    public void minimizeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage)this.minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage)this.closeButton.getScene().getWindow();
        stage.close();
    }

    public void accountButtonOnAction(ActionEvent event) {
        Stage stage = (Stage)this.accountButton.getScene().getWindow();
        stage.close();
        openUI.Open_UI("AccountManagementUI.fxml","");
    }

    public void homepageButtonOnAction(ActionEvent event) {
        Stage stage = (Stage)this.homepageButton.getScene().getWindow();
        stage.close();
        openUI.Open_UI("HomePageUI.fxml","");
    }

}