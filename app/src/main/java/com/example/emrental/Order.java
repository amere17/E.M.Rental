package com.example.emrental;

public class Order {
    private String price;
    private String location;
    private String type;
    private String userid;
    Order(){}
    Order(String m_location, String m_price, String m_type, String m_userid){
        this.location = m_location;
        this.price = m_price;
        this.type = m_type;
        this.userid = m_userid;
    }
    public void setPrice(String m_price){
        this.price = m_price;
    }
    public void setLocation(String m_location){
        this.location = m_location;
    }
    public void setType(String m_type){
        this.type = m_type;
    }
    public void setUserid(String m_userid){this.userid = m_userid;}
    public String getLocation(){return this.location;}
    public String getPrice(){return this.price;}
    public String getType(){return this.type;}
    public String getUserid(){return this.userid;}
}
