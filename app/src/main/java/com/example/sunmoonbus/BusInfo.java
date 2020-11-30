package com.example.sunmoonbus;

public class BusInfo {
    int userCount;
    String destination;
    double latitude;
    double longitude;
    int alrm;
    boolean moving;

    BusInfo() {

    }

    public void upDateInfo(String key, Object value) {
        try {
            switch (key) {
                case "desti":
                    this.destination = (String) value;
                    break;
                case "latitude":
                    this.latitude = (double) value;
                    break;
                case "longitude":
                    this.longitude = (double) value;
                    break;
                case "userCount":
                    this.userCount = ((Long) value).intValue();
                    break;
                case "alrm":
                    this.alrm = ((Long) value).intValue();
                    break;
                case "moving":
                     this.moving = (boolean) value;
                     break;

                default:
                    break;
            }

        } catch (ClassCastException e2) {
            e2.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
