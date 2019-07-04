package com;

import com.threads.ConnectToFolder;
import com.threads.Mailing;
import com.threads.Suicide;
import com.wss.WSSChatClient;

public class Main {

    private static int wss_i                  = 0;
    private static int db_i                   = 0;
    private static int count_problem_start    = 0;
    private static Mailing mailing            = null;
    public static Thread mailing_tread        = null;
    public static WSSChatClient wssChatClient = null;
    public static DB db                       = new DB();
    public static boolean is_restart          = true;

    public static void main(String[] args) {
        try {
            Thread suicide_thread = new Thread(new Suicide());
            suicide_thread.setPriority(Thread.MAX_PRIORITY);
            suicide_thread.start();

            if (db.connectToDB()) {
                db.cleanStatus();
            } else {
                System.out.println("Problem with DB");
                System.exit(0);
            }

//            wssChatClient = new WSSChatClient();
//
//            wssChatClient.connectToWSS();

            while (true) {
                if (db_i++ < 5 && (DB.result || db.connectToDB())) {
                    db_i = 0;
                }

                if (is_restart) {
                    if (mailing_tread != null && mailing_tread.isAlive()) {
                        mailing_tread.stop();
                    }
                    mailing = new Mailing();
                    mailing_tread = new Thread(mailing);
                    is_restart = false;
                }

                if (!mailing_tread.isAlive() ) {
                    mailing_tread.start();
                }

//                if (wssChatClient == null || WSSChatClient.result || Main.tryConnectToWSS()) {
//                    wss_i = 0;
//                }

//                System.out.println("main while");

//                System.out.println("-------------------------------- 1 count threads = " + ManagementFactory.getThreadMXBean().getThreadCount());


                if (ConnectToFolder.getCount_alive() > 50) {
                    System.gc();
                }

                if (Thread.activeCount() > 2000) {
                    count_problem_start++;

                    if (count_problem_start > 5) {
                        System.err.println("Exceeded the number of threads and the application failed to restart");
                        System.exit(0);
                    }

                    mailing_tread.stop();
//                    is_restart = true;

                    Thread.sleep(5000);

                    System.gc();

                    wssChatClient = new WSSChatClient();
                    mailing       = new Mailing();
                    mailing_tread = new Thread(mailing);
                }

                Thread.sleep(500);

                if (!suicide_thread.isAlive()) {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("<===================================================================================THE END + error===================================================================================>");
            System.exit(0);
        } finally {
//            System.out.println("<===================================================================================THE END===================================================================================>");
        }
    }

    public static boolean tryConnectToWSS() {

        boolean result = true;

        try {
            if (wssChatClient == null) {
                wssChatClient = new WSSChatClient();
                result = wssChatClient.connectToWSS();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }


}