package com.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Account implements Serializable {
    private User user;
    private ArrayList<MyFolder> myFolders = new ArrayList<>();
    private String status;

    public Account() {
        this.user   = null;
        this.status = "new";
    }

    public Account(User user) {
        this.user = user;
        this.status = "new";
    }

    public Account(User user, ArrayList<MyFolder> myFolders) {
        this(user, myFolders, "new");
    }

    public Account(User user, ArrayList<MyFolder> myFolders, String status) {
        this.user    = user;
        this.myFolders = myFolders;
        this.status  = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<MyFolder> getMyFolders() {
        return myFolders;
    }

    public void setMyFolders(ArrayList<MyFolder> myFolders) {
        this.myFolders = myFolders;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addMyFolder(MyFolder myFolder) {
        this.myFolders.add(myFolder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(user, account.user) &&
                Objects.equals(myFolders, account.myFolders) &&
                Objects.equals(status, account.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, myFolders, status);
    }

    @Override
    public String toString() {
        return "com.classes.Account{" +
                "user=" + user +
                ", myFolders=" + myFolders +
                ", status='" + status + '\'' +
                '}';
    }
}
