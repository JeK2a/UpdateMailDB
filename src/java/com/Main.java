package com;

import com.threads.ConnectToFolder;
import com.threads.Mailing;
import com.threads.MailingEmailAccountThread;
import com.wss.WSSChatClient;

public class Main {

    public static int main_i = 0;
    public static int wss_i  = 0;
    public static int db_i   = 0;

//    public static boolean

    public static DB db = new DB();
    public static WSSChatClient wssChatClient = new WSSChatClient();


    public static MailingEmailAccountThread mailingEmailAccount = null;


    public static Mailing mailing      = null;
    public static Thread mailing_tread = null;
    public static boolean is_restart = true;

    public static void main(String[] args) {
        try {
            if (db.connectToDB()) {
                db.cleanStatus();
            }

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

//                if (WSSChatClient.result || Main.tryConnectToWSS()) {
//                    wss_i = 0;
//                }

//                System.out.println("main while");

//                System.out.println("-------------------------------- 1 count threads = " + ManagementFactory.getThreadMXBean().getThreadCount());
                System.out.print("(" + Thread.activeCount() + "/" + ConnectToFolder.getCount_alive() + ") ");
                if (ConnectToFolder.getCount_alive() > 50) {
                    System.gc();
                }
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            if (main_i++ < 5) {
                main(null);
            }

            e.printStackTrace();
        }
    }

    public static boolean tryConnectToWSS() {

        boolean result = true;

        try {
            wssChatClient = new WSSChatClient();
            if (wssChatClient == null) {
                result = false;
                return false;
            }

            result = wssChatClient.connectToWSS();
        } catch (Exception e) {
            result = false;
        } finally {
            return result;
        }
    }


}
