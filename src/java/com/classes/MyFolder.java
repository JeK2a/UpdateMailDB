package com.classes;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.Objects;

public class MyFolder implements Cloneable {
    private String folder_name;
    private Thread threadAddNewMessages = null;
    private Thread threadLisaningChangeMessage = null;
    private String status;
    private Exception exception;
    private Timestamp last_event_time;
    private int event_counter;
    private IMAPFolder imap_folder;
    private long messages_count;

    private String email;

    public MyFolder(String folder_name, Thread threadAddNewMessages, String status, Timestamp last_event_time, int event_counter, IMAPFolder imap_folder) {
        this.folder_name = folder_name;
        this.threadAddNewMessages = threadAddNewMessages;
        this.status = status;
        this.last_event_time = last_event_time;
        this.event_counter = event_counter;
        this.imap_folder = imap_folder;

        try {
            this.messages_count = imap_folder.getMessageCount();
        } catch (MessagingException e) {
            setException(e);
            setStatus("error");
            e.printStackTrace();
        }
    }

    public MyFolder(IMAPFolder imap_folder) {
        this.folder_name = imap_folder.getFullName();
        this.status = "new";
        this.last_event_time = null;
        this.event_counter = 0;
        this.imap_folder = imap_folder;

        try {
            if (!imap_folder.isOpen()) {
                imap_folder.open(IMAPFolder.READ_ONLY);
            }
            this.messages_count = imap_folder.getMessageCount();
        } catch (MessagingException e) {
            setException(e);
            setStatus("error");
            e.printStackTrace();
        }
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public Thread getThreadAddNewMessages() {
        return threadAddNewMessages;
    }

    public void setThreadAddNewMessages(Thread threadAddNewMessages) {
        this.threadAddNewMessages = threadAddNewMessages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status; // TODO временно
    }

    public Timestamp getLast_event_time() {
        return last_event_time;
    }

    public void setLast_event_time(Timestamp last_event_time) {
        this.last_event_time = last_event_time;
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

    public Thread getThreadLisaningChangeMessage() {
        return threadLisaningChangeMessage;
    }

    public void setThreadLisaningChangeMessage(Thread threadLisaningChangeMessage) {
        this.threadLisaningChangeMessage = threadLisaningChangeMessage;
    }

    public void eventCountIncriminate() {
        this.event_counter++;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public long getMessages_count() {
        try {
            this.messages_count = imap_folder.getMessageCount();
        } catch (MessagingException e) {
            setException(e);
            setStatus("error");
            e.printStackTrace();
        }

        return messages_count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFolder myFolder = (MyFolder) o;
        return Objects.equals(folder_name, myFolder.folder_name) &&
               Objects.equals(threadAddNewMessages, myFolder.threadAddNewMessages) &&
               Objects.equals(imap_folder, myFolder.imap_folder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folder_name, threadAddNewMessages, imap_folder);
    }

    @Override
    public String toString() {
        return "MyFolder {" + "\n" +
               "                        folder_name                 = " + folder_name + "\n" +
               "                        threadAddNewMessages        = " + threadAddNewMessages + " name = " + threadAddNewMessages.getName() + " is_aleve = " + threadAddNewMessages.isAlive() + "\n" +
               "                        threadLisaningChangeMessage = " + threadLisaningChangeMessage + " name = " + threadLisaningChangeMessage.getName() + " is_aleve = " + threadLisaningChangeMessage.isAlive() + "\n" +
               "                        status                      = " + status + "\n" +
               "                        exception                   = " + exception + "\n" +
               "                        last_event_time             = " + last_event_time + "\n" +
               "                        event_counter               = " + event_counter + "\n" +
               "                        messages_counter            = " + messages_count + "\n" +
               "                        imap_folder                 = " + imap_folder + "\n" +
               "                        email                       = " + email + "\n" +
               "            }";
    }

    public MyFolder clone() throws CloneNotSupportedException {
        return (MyFolder) super.clone();
    }
}
