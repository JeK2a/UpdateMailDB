package com.classes;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.MessagingException;
import java.util.Date;

public class MyFolder implements Cloneable {
    private volatile String folder_name;
    private volatile String status = "new";
    private volatile String exception_text = "";
    private volatile int event_counter = 0;
    private volatile IMAPFolder imap_folder;
    private volatile long messages_db_count;
    private volatile long messages_count;

    private long time_status_change = new Date().getTime() / 1000;
    private long time_last_event    = new Date().getTime() / 1000;

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

    public long getMessages_db_count() {
        return messages_db_count;
    }

    public void setMessages_db_count(long messages_db_count) {
        this.messages_db_count = messages_db_count;
    }

    public void setMessages_count(long messages_count) {
        this.messages_count = messages_count;
    }

    private String email;

    public MyFolder(String folder_name, IMAPFolder imap_folder) {
        this.folder_name = folder_name;
        this.imap_folder = imap_folder;

        try {
            this.messages_count = imap_folder.getMessageCount();
        } catch (MessagingException e) {
            setException(e.toString());
            setStatus("error");
            e.printStackTrace();
        }
    }

    public MyFolder(IMAPFolder imap_folder) {
        this.folder_name     = imap_folder.getFullName();
        this.imap_folder     = imap_folder;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.updateTime_status_change();
        this.updateTime_last_event();
        this.status = status; // TODO временно
    }

    public int getEvent_counter() {
        return event_counter;
    }

    public void setEvent_counter(int event_counter) {
        this.event_counter = event_counter;
    }

    public IMAPFolder getImap_folder() {
        return imap_folder;
    }

    public void setImap_folder(IMAPFolder imap_folder) {
        this.imap_folder = imap_folder;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void eventCountIncriminate() {
        this.event_counter++;
    }

    public String getException_text() {
        return exception_text;
    }

    public void setException(String exception_text) {
        this.exception_text = exception_text;
    }

    public void setException(Exception exception) {
        this.exception_text = exception.toString();
    }

//    public long getMessages_count() {
//        try {
//            this.messages_count = imap_folder.getMessageCount();
//        } catch (MessagingException e) {
//            setException(e.toString()); // TODO
//            setStatus("error");
//            e.printStackTrace();
//        }
//
//        return messages_count;
//    }

    @Override
    public String toString() {
        return "{\"folder_name\": \""         + folder_name        + "\"," +
                "\"status\": \""              + status             + "\"," +
                "\"exception\": \""           + exception_text     + "\"," +
                "\"messages_counter\": \""    + messages_count     + "\"," +
                "\"messages_db_counter\": \"" + messages_db_count  + "\"," +
                "\"time_status_change\": "    + time_status_change + ","   +
                "\"time_last_event\": "       + time_last_event    +
                "}";
    }

    public MyFolder clone() throws CloneNotSupportedException {
        return (MyFolder) super.clone();
    }
}
