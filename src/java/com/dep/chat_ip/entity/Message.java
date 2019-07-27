package com.dep.chat_ip.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

    private long id;

    private Timestamp date;

    private String name;

    private String text;

    private String namePCAndIP;

    private String status;

    public Message(Timestamp date, String name, String text, String namePCAndIP, String status) {
        this.date = date;
        this.name = name;
        this.text = text;
        this.namePCAndIP = namePCAndIP;
        this.status = status;
    }

    public Message(String name, String text, String status) {
        this.date = new Timestamp(new Date().getTime());
        this.name = name;
        this.text = text;
        this.namePCAndIP = null;
        this.status = status;
    }

    public Message(String name, String text) {
        this.date =new Timestamp(new Date().getTime());
        this.name = name;
        this.text = text;
        this.namePCAndIP = null;
        this.status = null;
    }

    public long getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getNamePCAndIP() {
        return namePCAndIP;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return  name + ": " + text + " - " + new SimpleDateFormat("HH:mm:ss").format(date);
    }
}
