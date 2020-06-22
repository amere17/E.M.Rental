package com.example.emrental;

/**
 * Tool Class
 */
public class Tool {
    private String name;
    private String price;
    private String location;
    private String type;
    private String userid;
    private int status;
    private String address;

    /**
     * empty c-tor
     */
    Tool() {
    }

    /**
     * c-tor
     *
     * @param m_location  loation
     * @param m_name      name
     * @param m_price     price
     * @param m_type      type
     * @param m_userid    userId
     * @param m_available status
     * @param mAddress    address
     */
    Tool(String m_location, String m_name, String m_price, String m_type, String m_userid, int m_available, String mAddress) {
        this.name = m_name;
        this.location = m_location;
        this.price = m_price;
        this.type = m_type;
        this.userid = m_userid;
        this.status = m_available;
        this.address = mAddress;
    }

    /**
     * setter
     *
     * @param m_name tool name
     */
    public void setName(String m_name) {
        this.name = m_name;
    }

    /**
     * setter
     *
     * @param m_price price
     */
    public void setPrice(String m_price) {
        this.price = m_price;
    }

    /**
     * setter
     *
     * @param m_location location
     */
    public void setLocation(String m_location) {
        this.location = m_location;
    }

    /**
     * setter
     *
     * @param m_type type
     */
    public void setType(String m_type) {
        this.type = m_type;
    }

    /**
     * setter
     *
     * @param m_userid userID
     */
    public void setUserid(String m_userid) {
        this.userid = m_userid;
    }

    /**
     * getter
     *
     * @return tool name
     */
    public String getName() {
        return this.name;
    }

    /**
     * getter
     *
     * @return loation
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * getter
     *
     * @return price
     */
    public String getPrice() {
        return this.price;
    }

    /**
     * getter
     *
     * @return type
     */
    public String getType() {
        return this.type;
    }

    /**
     * getter
     *
     * @return userID
     */
    public String getUserid() {
        return this.userid;
    }

    /**
     * getter
     *
     * @return status
     */
    public int getAvailable() {
        return status;
    }

    /**
     * setter
     *
     * @param available status
     */
    public void setAvailable(int available) {
        this.status = available;
    }

    /**
     * getter
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * setter
     *
     * @param address address
     */
    public void setAddress(String address) {
        this.address = address;
    }

}
