package com.example.emrental;

public class Tool {
    private String name;
    private String price;
    private String location;
    private String type;
    private String userid;
    private int status;
    private String address;

    Tool() {
    }

    Tool(String m_location, String m_name, String m_price, String m_type, String m_userid ,int m_available,String mAddress) {
        this.name = m_name;
        this.location = m_location;
        this.price = m_price;
        this.type = m_type;
        this.userid = m_userid;
        this.status = m_available;
        this.address = mAddress;
    }

    public void setName(String m_name) {
        this.name = m_name;
    }

    public void setPrice(String m_price) {
        this.price = m_price;
    }

    public void setLocation(String m_location) {
        this.location = m_location;
    }

    public void setType(String m_type) {
        this.type = m_type;
    }

    public void setUserid(String m_userid) {
        this.userid = m_userid;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getPrice() {
        return this.price;
    }

    public String getType() {
        return this.type;
    }

    public String getUserid() {
        return this.userid;
    }

    public int getAvailable() {
        return status;
    }

    public void setAvailable(int available) {
        this.status = available;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
