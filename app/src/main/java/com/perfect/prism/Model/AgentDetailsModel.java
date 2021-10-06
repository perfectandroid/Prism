package com.perfect.prism.Model;

public class AgentDetailsModel {

    private String AgentName;
    private String ID_Client;
    private String Site;
    private String Location;
    private String AssignedTicket;
    private String SoftwarePending;
    private String ClosedTicket;
    private String Balance;
    private String ID_Agent;

    public AgentDetailsModel(){}

    public AgentDetailsModel(String AgentName,String ID_Client, String Site, String Location,
                             String AssignedTicket, String SoftwarePending, String ClosedTicket,
                             String Balance, String ID_Agent) {
        this.AgentName = AgentName;
        this.ID_Client = ID_Client;
        this.Site = Site;
        this.Location = Location;
        this.AssignedTicket = AssignedTicket;
        this.SoftwarePending = SoftwarePending;
        this.ClosedTicket = ClosedTicket;
        this.Balance = Balance;
        this.ID_Agent = ID_Agent;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }

    public String getID_Client() {
        return ID_Client;
    }

    public void setID_Client(String ID_Client) {
        this.ID_Client = ID_Client;
    }

    public String getSite() {
        return Site;
    }

    public void setSite(String site) {
        Site = site;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getAssignedTicket() {
        return AssignedTicket;
    }

    public void setAssignedTicket(String assignedTicket) {
        AssignedTicket = assignedTicket;
    }

    public String getSoftwarePending() {
        return SoftwarePending;
    }

    public void setSoftwarePending(String softwarePending) {
        SoftwarePending = softwarePending;
    }

    public String getClosedTicket() {
        return ClosedTicket;
    }

    public void setClosedTicket(String closedTicket) {
        ClosedTicket = closedTicket;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getID_Agent() {
        return ID_Agent;
    }

    public void setID_Agent(String ID_Agent) {
        this.ID_Agent = ID_Agent;
    }
}
