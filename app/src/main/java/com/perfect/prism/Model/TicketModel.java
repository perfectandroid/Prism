package com.perfect.prism.Model;

public class TicketModel {

    private String ID_Tickets;
    private String TickNo;
    private String SlNo;
    private String TickDate;
    private String TickSubject;
    private String TickPriority;
    private String TickStatus;
    private String UserDate;
    private String CliName;
    private String UserCount;
    private String AgentCount;
    private String Startingstatus;
    private String StartTime;
    private String EndTime;
    private String TotalTime;


    public TicketModel(String ID_Tickets, String TickNo, String SlNo, String TickDate,
                       String TickSubject,String TickPriority,String TickStatus,
                       String UserDate,String CliName,String UserCount,
                       String AgentCount,String Startingstatus,String StartTime,String EndTime,String TotalTime
                       ) {
        this.ID_Tickets = ID_Tickets;
        this.TickNo = TickNo;
        this.SlNo = SlNo;
        this.TickDate = TickDate;
        this.TickSubject = TickSubject;
        this.TickPriority = TickPriority;
        this.TickStatus = TickStatus;
        this.UserDate = UserDate;
        this.CliName = CliName;
        this.UserCount = UserCount;
        this.AgentCount = AgentCount;
        this.Startingstatus = Startingstatus;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.TotalTime = TotalTime;

    }

    public String getID_Tickets() {
        return ID_Tickets;
    }

    public void setID_Tickets(String ID_Tickets) {
        this.ID_Tickets = ID_Tickets;
    }

    public String getTickNo() {
        return TickNo;
    }

    public void setTickNo(String tickNo) {
        TickNo = tickNo;
    }

    public String getSlNo() {
        return SlNo;
    }

    public void setSlNo(String slNo) {
        SlNo = slNo;
    }

    public String getTickDate() {
        return TickDate;
    }

    public void setTickDate(String tickDate) {
        TickDate = tickDate;
    }

    public String getTickSubject() {
        return TickSubject;
    }

    public void setTickSubject(String tickSubject) {
        TickSubject = tickSubject;
    }

    public String getTickPriority() {
        return TickPriority;
    }

    public void setTickPriority(String tickPriority) {
        TickPriority = tickPriority;
    }

    public String getTickStatus() {
        return TickStatus;
    }

    public void setTickStatus(String tickStatus) {
        TickStatus = tickStatus;
    }

    public String getUserDate() {
        return UserDate;
    }

    public void setUserDate(String userDate) {
        UserDate = userDate;
    }

    public String getCliName() {
        return CliName;
    }

    public void setCliName(String cliName) {
        CliName = cliName;
    }

    public String getUserCount() {
        return UserCount;
    }

    public void setUserCount(String userCount) {
        UserCount = userCount;
    }

    public String getAgentCount() {
        return AgentCount;
    }

    public void setAgentCount(String agentCount) {
        AgentCount = agentCount;
    }

    public String getStartingstatus() {
        return Startingstatus;
    }

    public void setStartingstatus(String startingstatus) {
        Startingstatus = startingstatus;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getTotalTime() {
        return TotalTime;
    }

    public void setTotalTime(String totalTime) {
        TotalTime = totalTime;
    }

}
