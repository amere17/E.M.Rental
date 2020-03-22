package com.example.emrental;

import java.security.PrivateKey;

public class tools {
    private String name;
    private String price;
    private String location;
    private String type;
    private String userid;
    tools(){
    }
    tools(String m_location, String m_name, String m_price, String m_type, String m_userid){
        this.name = m_name;
        this.location = m_location;
        this.price = m_price;
        this.type = m_type;
        this.userid = m_userid;
    }
    public void setName(String m_name){
        this.name = m_name;
    }
    public void setPrice(String m_price){
        this.price = m_price;
    }
    public void setLocation(String m_location){
        this.location = m_location;
    }
    public void setType(String m_type){
        this.location = m_type;
    }
    public String getName(){return this.name;}
    public String getLocation(){return this.location;}
    public String getPrice(){return this.price;}
    public String getType(){return this.type;}


}
