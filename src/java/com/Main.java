package com;

import com.wss.WSSChatClient;

public class Main {

    public static int main_i = 0;
    public static int wss_i  = 0;
    public static int db_i   = 0;

//    public static boolean

    public static DB db = new DB();
    public static WSSChatClient wssChatClient = new WSSChatClient();


    public static MailingEmailAccount mailingEmailAccount = null;

    public static void main(String[] args) {
        try {

            if (db.connectToDB()) {

                db.cleanStatus();

                Mailing mailing = new Mailing();
                Thread mailing_tread = new Thread(mailing);
                mailing_tread.start();
            }

            while (true) {

//                if (wss_i++ < 5 && (WSSChatClient.result || Main.tryConnectToWSS())) {
//                    wss_i = 0;
//                }

                if (db_i++ < 5 && (DB.result || db.connectToDB())) {
                    db_i = 0;
                }

//                System.out.println("main while");
                Thread.sleep(500);
            }


        } catch (Exception e) {
            if (main_i++ < 5) {
                main(null);
            }

            e.printStackTrace();
        } finally {

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

    public static boolean tryConnectToEmailAccounts() {

        boolean result = true;

        try {

        } catch (Exception e) {
            result = false;
        } finally {
            return result;
        }


    }

}
