package com.example.sunmoonbus;

public class BusInfo {
    String id;
    int userCount;
    String Destination;
    double latitude;
    double longitude;
    boolean moving;
    BusInfo() {

    }

    public void upDateInfo(String key, Object value) {
        this.id = key;
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
                case "moving":
                    this.moving = (boolean) value;
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
