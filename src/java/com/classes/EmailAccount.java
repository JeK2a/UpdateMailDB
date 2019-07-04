package com.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EmailAccount implements Serializable {
    private volatile User user;
    private volatile ConcurrentHashMap<String, MyFolder> myFoldersMap = new ConcurrentHashMap<>();
    private volatile String status = "new";
    private volatile int event_count = 0;
    private volatile String emailAddress;
//    private volatile Thread threadAccount;
//    private volatile Exception exception;
    private volatile String exception_text = "";

    private volatile long time_status_change = new Date().getTime() / 1000;
    private volatile long time_last_event    = new Date().getTime() / 1000;


    public EmailAccount(User user) {
        this.user         = user;
        this.emailAddress = user.getEmail();
    }

    public EmailAccount(User user, ConcurrentHashMap<String, MyFolder> myFoldersMap) {
        this.user         = user;
        this.myFoldersMap = myFoldersMap;
    }

    public EmailAccount(User user, ConcurrentHashMap<String, MyFolder> myFoldersMap, String status) {
        this.user         = user;
        this.emailAddress = user.getEmail();
        this.myFoldersMap = myFoldersMap;
        this.status       = status;
    }

    public String getException_text() {
        return exception_text;
    }

    public void setException(String exception_text) {
        this.setStatus("error");
        this.exception_text = exception_text;
    }

    public void setException(Exception exception) {
        setException(exception.toString());
    }

    public void updateTime_status_change() {
        this.time_status_change = new Date().getTime() / 1000;
    }

    public void updateTime_last_event() {
        this.time_last_event = new Date().getTime() / 1000;
    }

    public long getTime_status_change() {
        return time_status_change;
    }

    public void setTime_status_change(long time_status_change) {
        this.time_status_change = time_status_change;
    }

    public long getTime_last_event() {
        return time_last_event;
    }

    public void setTime_last_event(long time_last_event) {
        this.time_last_event = time_last_event;
    }

    public ConcurrentHashMap<String, MyFolder> getFoldersMap() {
        return myFoldersMap;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ConcurrentHashMap<String, MyFolder> getMyFoldersMap() {
        return myFoldersMap;
    }

    public void setMyFoldersMap(ConcurrentHashMap<String, MyFolder> myFoldersMap) {
        this.myFoldersMap = myFoldersMap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.updateTime_status_change();
        this.updateTime_last_event();
        this.status = status;
    }

    public void addMyFolder(MyFolder myFolder) {
        this.myFoldersMap.put(myFolder.getFolder_name(), myFolder);
    }

    public int getEvent_count() {
        return event_count;
    }

    public void setEvent_count(int event_count) {
        this.event_count = event_count;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void eventCounterIncriminate() {
        this.event_count++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAccount emailAccount = (EmailAccount) o;
        return Objects.equals(user, emailAccount.user) &&
               Objects.equals(myFoldersMap, emailAccount.myFoldersMap) &&
               Objects.equals(status, emailAccount.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, myFoldersMap, status);
    }

    @Override
    public String toString() {
        return "{\"user\": "                   + user                         +
                ", \"status\": \""             + status                       +
                "\", \"error\": \""            +  exception_text              +
                "\", \"myFoldersMap\": "       + getJsonFromMap(myFoldersMap) +
                ", \"time_status_change\": "   + time_status_change           +
                ", \"time_last_event\": "      + time_last_event              +
                "}";
    }

    private String getJsonFromMap(ConcurrentHashMap<String, MyFolder> map) {
        StringBuffer tmpStr = new StringBuffer("{ ");

        for(Map.Entry<String, MyFolder> e: map.entrySet()){
//            System.out.println("start " + e.getKey());
//            System.out.println(e.getKey() + " 1");
            tmpStr.append("\"").append(e.getKey()).append("\": ").append(e.getValue()).append(",");
//            System.out.println(e.getKey() + " 2");
//            System.out.println("end " + e.getKey());
        }

        return tmpStr.substring(0, tmpStr.length() - 1) + "}";
    }

}
