package com.perfect.prism.Model;

public class ClientHomeModel {

    private String ID_Client;
    private String ClientName;
    private String ClientCount;
    private String FK_Product;

    public ClientHomeModel(String ID_Client, String ClientName, String ClientCount,String FK_Product) {
        this.ClientName = ClientName;
        this.ID_Client = ID_Client;
        this.ClientCount = ClientCount;
        this.FK_Product = FK_Product;
    }

    public String getID_Client() {
        return ID_Client;
    }

    public void setID_Client(String ID_Client) {
        this.ID_Client = ID_Client;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }
    public String getClientCount() {
        return ClientCount;
    }

    public void setClientCount(String ClientCount) {
        ClientCount = ClientCount;
    }

    public String getFK_Product() {
        return FK_Product;
    }

    public void setFK_Product(String FK_Product) {
        this.FK_Product = FK_Product;
    }
}
