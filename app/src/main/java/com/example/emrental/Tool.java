package com.example.emrental;

import java.util.Vector;

public class Tool {
        //String toolId;
        String name;
        String price;
        String type;
        String Latitude;
        String Longitude;


    public Tool() {} // Empty constructor is required
        public Tool(String name, String price,String type, String mLatitude, String mLongitude) {
            //this.toolId = toolId;
            this.name = name;
            this.price = price;
            this.type = type;
            this.Latitude = mLatitude;
            this.Longitude = mLongitude;
        }

        //public String getTooltId() { return this.toolId; }
        //public void setToolId(String toolId) { this.toolId = toolId; }
        public String getName() { return this.name; }
        public void setName(String name) { this.name = name; }
        public String getPrice() { return this.price; }
        public void setPrice(String price) { this.price= price; }
        public String getType() { return this.type; }
        public void setType(String type) { this.type = type; }
        public void setLocation(String mLong, String mLat){ this.Longitude = mLong; this.Latitude = mLat;}
        public Vector<String> getLocation(){
            Vector<String> mLocation = new Vector<>();
            mLocation.add(this.Longitude);
            mLocation.add(this.Latitude);
            return mLocation;
        }


}
