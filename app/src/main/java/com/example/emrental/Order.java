package com.example.emrental;

public class Order {
    private String Owner;
    private String User;
    private String ToolId;
    private String Status;
    private String start;
    private String end;
    private String totalPrice;
    Order(){}
    Order(String m_Owner, String m_User, String m_Tool, String status,String start,String end,String totalPrice){
        this.Owner =m_Owner;
        this.User = m_User;
        this.Status = status;
        this.ToolId= m_Tool;
        this.start= start;
        this.end= end;
        this.totalPrice= totalPrice;
    }
    public void setEnd(String end) {
        this.end = end;
    }
    public void setOwner(String owner) {
        Owner = owner;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public void setStatus(String status) {
        Status = status;
    }
    public void setToolId(String toolId) {
        ToolId = toolId;
    }
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
    public void setUser(String user) {
        User = user;
    }
    public String getEnd() {
        return end;
    }
    public String getOwner() {
        return Owner;
    }
    public String getStart() {
        return start;
    }
    public String getStatus() {
        return Status;
    }
    public String getToolId() {
        return ToolId;
    }
    public String getTotalPrice() {
        return totalPrice;
    }
    public String getUser() {
        return User;
    }
}
