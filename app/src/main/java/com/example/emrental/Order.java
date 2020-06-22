package com.example.emrental;

/**
 * Order Class
 * contains members of order details
 */
public class Order {
    private String Owner;
    private String User;
    private String ToolId;
    private String Status;
    private String start;
    private String end;
    private String totalPrice;

    /**
     * c-tor
     */
    Order() {
    }

    Order(String m_Owner, String m_User, String m_Tool, String status, String start, String end, String totalPrice) {
        this.Owner = m_Owner;
        this.User = m_User;
        this.Status = status;
        this.ToolId = m_Tool;
        this.start = start;
        this.end = end;
        this.totalPrice = totalPrice;
    }

    /**
     * setter - end of order date:time
     *
     * @param end end date:time
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * setter - set tool owner ID
     *
     * @param owner name
     */
    public void setOwner(String owner) {
        Owner = owner;
    }

    /**
     * setter - start of order date:time
     *
     * @param start start date:time
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * setter - order status
     *
     * @param status status
     */
    public void setStatus(String status) {
        Status = status;
    }

    /**
     * setter - tool ID
     *
     * @param toolId tool ID
     */
    public void setToolId(String toolId) {
        ToolId = toolId;
    }

    /**
     * setter - total price
     *
     * @param totalPrice total price
     */
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * setter - user ID
     *
     * @param user user ID
     */
    public void setUser(String user) {
        User = user;
    }

    /**
     * getter
     *
     * @return order end date:time
     */
    public String getEnd() {
        return end;
    }

    /**
     * getter - owner
     *
     * @return owner ID
     */
    public String getOwner() {
        return Owner;
    }

    /**
     * getter
     *
     * @return order start date:time
     */
    public String getStart() {
        return start;
    }

    /**
     * getter
     *
     * @return order status
     */
    public String getStatus() {
        return Status;
    }

    /**
     * getter
     *
     * @return tool ID
     */
    public String getToolId() {
        return ToolId;
    }

    /**
     * getter
     *
     * @return total Price
     */
    public String getTotalPrice() {
        return totalPrice;
    }

    /**
     * getter
     *
     * @return user ID
     */
    public String getUser() {
        return User;
    }


}
