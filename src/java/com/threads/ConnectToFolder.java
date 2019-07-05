package com.threads;

import javax.mail.Folder;

public class ConnectToFolder implements Runnable {

    private Folder folder;
    public boolean is_open = false;
    private static int count_alive = 0;

    public ConnectToFolder(Folder folder) {
        this.folder = folder;
    }

    @Override
    public void run() {
        System.out.println(count_alive + " + 1 lvl 1");
        count_alive++;

        ConnectToFolder_lvl2 connectToFolder_lvl2 = new ConnectToFolder_lvl2(folder);

        Thread thread = new Thread(connectToFolder_lvl2);

        thread.setDaemon(true);

        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        is_open = connectToFolder_lvl2.is_open;

//        try {
//            if (!folder.isOpen()) {
////                Store store = folder.getStore();
//                is_open = false;
//                folder.open(Folder.READ_ONLY);
//            }
//            is_open = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println(count_alive + " - 1");
//            count_alive--;
//        }

        System.out.println(count_alive + " - 1 lvl 1");
            count_alive--;

    }

    public static int getCount_alive() {
        return count_alive;
    }
}