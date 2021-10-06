package com.perfect.prism.Model;

public class ClientModel {

    private String ID_Client;
    private String ClientName;

    public ClientModel(String ID_Client, String ClientName) {
        this.ClientName = ClientName;
        this.ID_Client = ID_Client;
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
}
