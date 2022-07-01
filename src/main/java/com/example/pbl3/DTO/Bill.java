package com.example.pbl3.DTO;

import java.sql.Date;

public class Bill {
    private int ID_Bill;
    private int ID_Customer;
    private String customerName;
    private String cashier_gmail;
    private String cashierName;
    private Date date;
    private double Total_Money;
    private String pay;
    private boolean status;

    public Bill(int ID_Bill, int ID_Customer, String customerName, String cashier_gmail, String cashierName, Date date, double total_Money, String pay, boolean status) {
        this.ID_Bill = ID_Bill;
        this.ID_Customer = ID_Customer;
        this.customerName = customerName;
        this.cashier_gmail = cashier_gmail;
        this.cashierName = cashierName;
        this.date = date;
        this.Total_Money = total_Money;
        this.pay = pay;
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCashierName() {
        return cashierName;
    }

    public int getID_Bill() {
        return ID_Bill;
    }

    public int getID_Customer() {
        return ID_Customer;
    }

    public String getCashier_gmail() {
        return cashier_gmail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotal_Money() {
        return Total_Money;
    }

    public String getPay() {
        return pay;
    }

    public boolean isStatus() {
        return status;
    }
}
