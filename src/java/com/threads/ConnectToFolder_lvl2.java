package com.threads;

import javax.mail.Folder;

public class ConnectToFolder_lvl2 implements Runnable {

    private Folder folder;
    public boolean is_open = false;
    private static int count_alive = 0;

    public ConnectToFolder_lvl2(Folder folder) {
        this.folder = folder;
    }

    @Override
    public void run() {
        System.out.println(count_alive + " + 1 lvl 2");
        count_alive++;
        
        try {
            if (!folder.isOpen()) {
//                Store store = folder.getStore();
                is_open = false;
                folder.open(Folder.READ_ONLY);
            }
            is_open = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(count_alive + " - 1 lvl 2");
            count_alive--;
        }
    }

    public static int getCount_alive() {
        return count_alive;
    }
}