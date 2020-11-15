package com.example.sunmoonbus;

public class BusInfo {
    int userCount;
    String Destination;
    double latitude;
    double longitude;

    BusInfo() {

    }

    public void upDateInfo(String key, Object value) {
        try {
            switch (key) {
                case "desti":
                    this.Destination = (String) value;
                    break;
                case "latitude":
                    this.latitude = (double) value;
                    break;
                case "longitude":
                    this.longitude = (double) value;
                    break;
                case "userCount":
                    this.userCount = ((Long) value).intValue();
                default:
                    break;
            }

        } catch (ClassCastException e2) {
            e2.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            //System.out.println(this.Destination + " " + this.latitude + " " +  this.longitude + " " + this.userCount);
        }
    }
}
