package com.example.jailor;

public class Device {
    private String Name, IP, Description;

    public String getName() {
        return Name;
    }
    public String getIP() {
        return IP;
    }
    public String getDescription() {
        return Description;
    }
    public void setName(String name) {
        Name = name;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }
    public void setDescription(String description) {
        Description = description;
    }

    public Device(String name, String ip, String  description){
        setName(name);
        setIP(ip);
        setDescription(description);
    }

    @Override
    public String toString() {
        return this.Name + " " + this.IP; // What to display in the Spinner list.
    }

}
