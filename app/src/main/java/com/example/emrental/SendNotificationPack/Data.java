package com.example.emrental.SendNotificationPack;

public class Data {
    private String Title;
    private String Message;
    private String UserID;
    private String ToolID;
    private String ToolName;

    public Data(String title, String message,String userID,String toolID,String toolName) {
        Title = title;
        Message = message;
        UserID = userID;
        ToolID = toolID;
        ToolName = toolName;
    }

    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getToolID() {
        return ToolID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setToolID(String toolID) {
        ToolID = toolID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setToolName(String toolName) {
        ToolName = toolName;
    }

    public String getToolName() {
        return ToolName;
    }
}
