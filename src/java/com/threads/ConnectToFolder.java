package com.threads;

import javax.mail.Folder;
import javax.mail.Store;

public class ConnectToFolder implements Runnable {

    private Folder folder;
    private Store store;
    public boolean is_open = false;
    private static int i = 0;
    private static int count_alive = 0;
    private String mark;

    public ConnectToFolder(Folder folder, String mark) {
        this.folder = folder;
        this.mark   = mark;
    }

    @Override
    public void run() {
//        int tmp_i = ++i;
        count_alive++;
        
        try {
            if (!folder.isOpen()) {
                store   = folder.getStore();
                is_open = false;
                folder.open(Folder.READ_ONLY);
            }
            is_open = true;
        } catch (Exception e) {
//            System.err.println(tmp_i + " ConnectToFolder error");
//            System.err.println(folder.getFullName());
            e.printStackTrace();
        } finally {
//            System.out.println(tmp_i + " ConnectToFolder finally");
            count_alive--;
        }
    }

    public static int getCount_alive() {
        return count_alive;
    }
}
