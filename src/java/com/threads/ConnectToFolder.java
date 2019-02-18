package com.threads;

import javax.mail.Folder;
import javax.mail.MessagingException;

public class ConnectToFolder implements Runnable {

    private Folder folder;
    public boolean is_open = false;

    public ConnectToFolder(Folder folder) {
        this.folder = folder;
    }

    @Override
    public void run() {
        try {
            if (!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }
            is_open = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
