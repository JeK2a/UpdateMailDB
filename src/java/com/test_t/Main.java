package com.test_t;

import java.lang.management.ManagementFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("-------- 1 count threads = " + ManagementFactory.getThreadMXBean().getThreadCount());
        System.out.println("-------- 2 count threads = " + Thread.activeCount());

        Thread thread;

        while (true) {

            try {

                System.out.println("-------- 1 count threads = " + ManagementFactory.getThreadMXBean().getThreadCount());
                System.out.println("-------- 2 count threads = " + Thread.activeCount());

                thread = new Thread(new Testtt());

                thread.start();
                Thread.sleep(1000);

                System.out.println("----------- 1 count threads = " + ManagementFactory.getThreadMXBean().getThreadCount());
                System.out.println("----------- 2 count threads = " + Thread.activeCount());

//                thread.stop();
                thread.interrupt();

            System.out.println("-------------- 1 count threads = " + ManagementFactory.getThreadMXBean().getThreadCount());
            System.out.println("-------------- 2 count threads = " + Thread.activeCount());


//            } catch (InterruptedException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


class Testtt implements Runnable {

    @Override
    public void run() {

        System.out.println("Thander");
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("E");
        } finally {
            System.out.println('F');
        }
    }
}
