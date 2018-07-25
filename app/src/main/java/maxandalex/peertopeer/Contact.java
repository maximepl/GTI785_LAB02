package maxandalex.peertopeer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Contact {
    private String ip;
    private String id;
    private String name;
    private boolean online;
    private double distance;
    //private long lastLogin;

    public static ArrayList<Contact> contactList = new ArrayList<>();

    public Contact(String id, String name, String ip) {

        Random r = new Random();
        double randomValue = 100 + (200 - 100) * r.nextDouble();

        this.id = id;
        this.ip = ip;
        this.name = name;
        this.online = true;
        this.distance = Double.parseDouble(new DecimalFormat("##.##").format(randomValue));
    }

    //public Contact(String information) {
    //    String[] info = information.split(":");
    //    this.id = info[0];
    //    this.ip = info[1];
    //}

    /*public Contact(String id, String ip) {
        this.id = id;
        this.ip = ip;
        online = false;
        distance = 0;
        lastLogin = 0;
    }*/


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isOnline(){
        return online;
    }

    public void setOnline(boolean online){
        this.online = online;
    }

    public double getDistance(){
        return this.distance;
    }

    public void setDistance(Double distance){
        this.distance = distance;
    }

    /*public long getLastLogin(){
        return lastLogin;
    }

    public void setLastLogin(Long lastLogin){
        this.lastLogin = lastLogin;
    }*/
}
