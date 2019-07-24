package com;

import com.db.DB;
import com.threads.Mailing;
import com.threads.Suicide;

public class Main {

    private static int db_i                   = 0;
    public static Thread mailing_tread        = null;
    public static DB db                       = new DB();
    public static boolean is_restart          = true;

    public static void main(String[] args) {
        try {
            if (!db.connectToDB()) {
                System.exit(0);
            }

            Thread suicide_thread = new Thread(new Suicide());
            suicide_thread.setPriority(Thread.MAX_PRIORITY);
            suicide_thread.start();

            mailing_tread = new Thread(new Mailing());
            mailing_tread.start();

            while (true) {
                if (!suicide_thread.isAlive()) {
                    System.err.println("Suicide died");
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