package com;

import com.db.DB;
import com.threads.Mailing;
import com.threads.Suicide;
import com.wss.WSSChatClient;

public class Main {

    private static int db_i                   = 0;
    public static Thread mailing_tread        = null;
    public static WSSChatClient wssChatClient = null;
    public static DB db                       = new DB();
    public static boolean is_restart          = true;

    public static void main(String[] args) {
        try {
            Thread suicide_thread = null;

            if (!db.connectToDB()) {
                System.err.println("Problem with DB");
                System.exit(0);
            }

            mailing_tread = new Thread(new Mailing());
            mailing_tread.start();

            while (true) {
                if (suicide_thread == null || !suicide_thread.isAlive()) {
                    suicide_thread = new Thread(new Suicide());
                    suicide_thread.setPriority(Thread.MAX_PRIORITY);
                    suicide_thread.start();
                }

                if (db_i++ < 5 && (DB.result || db.connectToDB())) {
                    db_i = 0;
                }

                Thread.sleep(500);

                if (!suicide_thread.isAlive()) {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("<===================================================================================THE END + error===================================================================================>");
            System.exit(0);
        } finally {
            System.out.println("<===================================================================================THE END===================================================================================>");
        }
    }



}