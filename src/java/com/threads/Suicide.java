package com.threads;

import com.wss.WSSChatClient;

public class Suicide implements Runnable {
    @Override
    public void run() {
        long timer = 0;
        long time_limit = 60 * 60 * 3;

        WSSChatClient wssChatClient = new WSSChatClient();
        wssChatClient.connectToWSS();

        while (true) {
            System.out.print(" timer " + timer + " (" + Thread.activeCount() + "/" + ConnectToFolder.getCount_alive() + ") ");

            if (
                    timer++ > time_limit                    ||
                    Thread.activeCount()             > 2000 ||
                    ConnectToFolder.getCount_alive() > 2000
            ) {
                System.out.println("=================================================Suicide=================================================");
                System.exit(0);
            }

            if (timer % 15 == 0) {
                System.out.println("wss restart !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                WSSChatClient.getWebSocket().sendClose();
                wssChatClient.connectToWSS();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
