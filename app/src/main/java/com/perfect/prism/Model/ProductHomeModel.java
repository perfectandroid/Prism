package com.perfect.prism.Model;

public class ProductHomeModel {

    private String ID_Product;
    private String ProductName;
    private String ProductCount;


    public ProductHomeModel(String ID_Product, String ProductName, String ProductCount) {
        this.ProductName = ProductName;
        this.ID_Product = ID_Product;
        this.ProductCount = ProductCount;
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
    public String getProductCount() {
        return ProductCount;
    }
    public void setProductCount(String ProductCount) {
        this.ProductCount = ProductCount;
    }
}
