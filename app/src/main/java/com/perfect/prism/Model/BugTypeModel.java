package com.perfect.prism.Model;

public class BugTypeModel {

    private String ID_Product;
    private String ProductName;

    public BugTypeModel(String ID_Product, String ProductName) {
        this.ProductName = ProductName;
        this.ID_Product = ID_Product;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getID_Product() {
        return ID_Product;
    }

    public void setID_Product(String ID_Product) {
        this.ID_Product = ID_Product;
    }
}
