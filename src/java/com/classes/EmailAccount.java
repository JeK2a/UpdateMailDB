package com.classes;

import com.service.MyPrint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class EmailAccount implements Serializable {
    private User user;
    private HashMap<String, MyFolder> myFoldersMap = new HashMap<>();
    private String status;
    private Exception exception;
    private int event_count = 0;
    private String emailAddress;
    private Thread threadAccount;

    public EmailAccount(User user) {
        this.user = user;
        this.emailAddress = user.getEmail();
        this.status = "new";
    }

    public EmailAccount(User user, HashMap<String, MyFolder> myFoldersMap) {
        this(user, myFoldersMap, "new");
    }

    public EmailAccount(User user, HashMap<String, MyFolder> myFoldersMap, String status) {
        this.user         = user;
        this.myFoldersMap = myFoldersMap;
        this.status       = status;
        this.emailAddress = user.getEmail();
    }
    public HashMap<String, MyFolder> getFoldersMap() {
        return myFoldersMap;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HashMap<String, MyFolder> getMyFoldersMap() {
        return myFoldersMap;
    }

    public void setMyFoldersMap(HashMap<String, MyFolder> myFoldersMap) {
        this.myFoldersMap = myFoldersMap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        System.err.println("setStatus " + status);
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
        return "EmailAccount { \n"    +
               "      user = " + user         + "\n" +
               "      status       = " + status       + "\n" +
               "      myFoldersMap = " + MyPrint.getStringHashMap(myFoldersMap) + "\n" +
               "} \n";
    }

}
