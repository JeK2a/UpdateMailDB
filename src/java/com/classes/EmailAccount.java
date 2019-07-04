package com.classes;

import java.io.Serializable;
import java.sql.Timestamp;
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
    private volatile Thread threadAccount;
    private volatile Exception exception;

    private volatile Timestamp time_status_change = new Timestamp(new Date().getTime());
    private volatile Timestamp time_last_event    = new Timestamp(new Date().getTime());

    public Timestamp getTime_status_change() {
        return time_status_change;
    }

    public void setTime_status_change(Timestamp time_status_change) {
        this.time_last_event    = new Timestamp(new Date().getTime());
        this.time_status_change = time_status_change;
    }

    public Timestamp getTime_last_event() {
        return time_last_event;
    }

    public void setTime_last_event(Timestamp time_last_event) {
        this.time_last_event = time_last_event;
    }

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
//        System.err.println("setStatus " + status);
        this.time_last_event    = new Timestamp(new Date().getTime());
        this.time_status_change = new Timestamp(new Date().getTime());
        this.status = status;
    }

    public void addMyFolder(MyFolder myFolder) {
        this.myFoldersMap.put(myFolder.getFolder_name(), myFolder);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
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

    public Thread getThreadAccount() {
        return threadAccount;
    }

    public void setThreadAccount(Thread threadAccount) {
        this.threadAccount = threadAccount;
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
        return "{\"user\": "                   + user                                                       +
                ", \"status\": \""             + status                                                     +
//                "\", \"error\": \""            + (exception == null ? "" : exception.toString())            +
                "\", \"error\": \""            +  ""             +
                "\", \"myFoldersMap\": "       + getJsonFromMap(myFoldersMap)                                            +
                ", \"time_status_change\": " + 1562237766                        +
//                ", \"time_status_change\": \"" + time_status_change.getTime() / 1000                        +
                ", \"time_last_event\": "  + 1562237766                       +
//                "\", \"time_last_event\": \""  + time_last_event.getTime()    / 1000                        +
//                "\", \"thread_name\": \""      + (threadAccount == null ? "null" : threadAccount.getName()) +
                "}";
    }


    private String getJsonFromMap(ConcurrentHashMap<String, MyFolder> map) {
        StringBuffer tmpStr = new StringBuffer("{");
//        StringBuilder tmpStr = new StringBuilder("{");

        for(Map.Entry<String, MyFolder> e: map.entrySet()){
            System.out.println("start " + e.getKey());
            System.out.println(e.getKey() + " 1");
            tmpStr.append("\"").append(e.getKey()).append("\": ").append(e.getValue()).append(",");
            System.out.println(e.getKey() + " 2");
            System.out.println("end " + e.getKey());
        }

        tmpStr.substring(0, tmpStr.length() - 1);

        String tmp = tmpStr.substring(0,  tmpStr.length() - 1);

        if (tmp.equals("")) {
            tmp = "{}";
        } else {
            tmp += "}";
        }



        return tmp;
    }

}
