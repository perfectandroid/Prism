package com.perfect.prism.Model;

public class AgentModel {

    private String ID_Agent;
    private String AgentName;

    public AgentModel(String ID_Agent, String AgentName) {
        this.AgentName = AgentName;
        this.ID_Agent = ID_Agent;
    }

    public String getID_Agent() {
        return ID_Agent;
    }

    public void setID_Agent(String ID_Agent) {
        this.ID_Agent = ID_Agent;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }
}
