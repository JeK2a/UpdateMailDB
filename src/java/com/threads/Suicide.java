package com.threads;

public class Suicide implements Runnable {
    @Override
    public void run() {
        long timer = 0;
        long time_limit = 60*60*3;

//        System.out.println(timer + " / " + Thread.activeCount() + " / " + ConnectToFolder.getCount_alive());

        while (true) {
            System.out.print(" timer " + timer + " (" + Thread.activeCount() + "/" + ConnectToFolder.getCount_alive() + ") ");

            if (
                    timer++ > time_limit        ||
                    Thread.activeCount() > 2000 ||
                    ConnectToFolder.getCount_alive() > 2000
            ) {
                System.out.println("=================================================Suicide=================================================");
                System.exit(0);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
