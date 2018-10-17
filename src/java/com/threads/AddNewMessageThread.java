package com.threads;

import com.DB;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.MyMessage;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddNewMessageThread implements Runnable {

    private DB db;
    private EmailAccount emailAccount;
    private IMAPFolder imap_folder;
    private MyFolder myFolder;

    private String myEx;

    public AddNewMessageThread(EmailAccount emailAccount, MyFolder myFolder) {
        db = new DB();
        this.emailAccount = emailAccount;
        this.myFolder = myFolder;
        this.imap_folder = myFolder.getImap_folder();
    }

    @Override
    public void run() {
        try {
            int messages_count_mail = imap_folder.getMessageCount();
            int messages_count_db = db.getCountMessages(emailAccount.getUser().getUser_id(), myFolder.getFolder_name());

            System.err.println("messages_count_mail = " + messages_count_mail);
            System.err.println("messages_count_db   = " + messages_count_db);

            Message[] messages;

            if (messages_count_db > 0) {

                int user_id = emailAccount.getUser().getUser_id();
                String folder_name = myFolder.getFolder_name();

                // Если нет UID, то пометить как deleted
                // 4
                int query_buffer = 1000;
                int query_count = (messages_count_db / query_buffer) + 1;

                System.err.println(query_count);

                for (int j = 0; j < query_count; j++) {

                    int start = (j * query_buffer + 1);
                    int end = ((j + 1) * query_buffer + 1);

                    if (end > messages_count_db) {
                        end = messages_count_db;
                    }
                    if (start > end) {
                        start = end;
                    }

                    int finalEnd = end;
                    int finalStart = start;

                    ArrayList<Long> arr_uids = (ArrayList<Long>) imap_folder.doCommand(imapProtocol -> {
                        Response[] responses;
                        ArrayList<Long> arr_uids_tmp = new ArrayList<>();

                        responses = imapProtocol.command("UID SEARCH " + finalStart + ":" + finalEnd, null);

                        String[] out_str = responses[0].toString().split(" ");

                        if (out_str.length > 2) {
                            for (int n = 2; n < out_str.length; n++) {
                                arr_uids_tmp.add(Long.parseLong(out_str[n]));
                            }
                        }

                        return arr_uids_tmp;
                    });

                    if (arr_uids.size() > 0) {
                        long uid_start = arr_uids.get(0);
                        long uid_end = arr_uids.get(arr_uids.size() - 1);

                        String str_uids = String.valueOf(arr_uids.get(0));
                        for (int n = 1; n < arr_uids.size(); n++) {
                            str_uids += "," + String.valueOf(arr_uids.get(n));
                        }

                        db.checkDelete(user_id, folder_name, uid_start, uid_end, str_uids); //set removed flag
                    }
                }

                // 4 END

                // 1
                if (messages_count_mail < 1) {
                    myEx = imap_folder.getFullName() + " - message count = " + messages_count_mail;
                    System.err.println(myEx);
                    return;
                }

                int part_count = (int) Math.ceil(Math.sqrt(messages_count_db) / 2);

                ArrayList<MyMessage> myMessages = db.getRandomMessages(
                        emailAccount.getUser().getUser_id(),
                        myFolder.getFolder_name(),
                        part_count
                );
                long[] uids = new long[part_count];
                int i = 0;

                for (MyMessage myMessage : myMessages) {
                    if (myMessage.getUid() > 0) {
                        uids[i++] = myMessage.getUid();
                    }
                }

                Message[] messages_tmp = new Message[0];

                if (messages_count_db > 0) {
                    messages_tmp = imap_folder.getMessagesByUID(uids);
                }

                int check_count = 0;

                i = 0;

                for (MyMessage myMessage : myMessages) {
                    if (myMessage.compare((IMAPMessage) messages_tmp[i++], imap_folder)) {
                        check_count++;
                    }
                }

                //2
                //todo ilya  update flags in both cases
//            if (check_count != part_count) {

                db.setFlags(emailAccount.getUser().getUser_id(), imap_folder.getFullName());

                HashMap<String, String> flags = new HashMap<>();

                flags.put("KEYWORD $Forwarded", null);
                flags.put("KEYWORD $label1", null);
                flags.put("KEYWORD $label2", null);
                flags.put("KEYWORD $label3", null);
                flags.put("KEYWORD $label4", null);
                flags.put("KEYWORD $label5", null);
                flags.put("KEYWORD $HasAttachment", null);
                flags.put("FLAGGED", null);
                flags.put("ANSWERED", null);
                flags.put("DELETED", null);
                flags.put("DRAFT", null);
//                flags.put("RECENT", null);
                flags.put("UNSEEN", null);

                for (HashMap.Entry<String, String> flag : flags.entrySet()) {
                    flag.setValue((String) imap_folder.doCommand(imapProtocol -> {
                        String str_uids = "";
                        Response[] responses = imapProtocol.command("UID SEARCH " + flag.getKey(), null);
                        String[] arr_out_str = responses[0].toString().split(" ");

                        if (arr_out_str.length > 2) {
                            str_uids = arr_out_str[2];

                            for (int n = 3; n < arr_out_str.length; n++) {
                                str_uids += "," + arr_out_str[n];
                            }
                        }

                        return str_uids;
                    }));
                }

                for (HashMap.Entry<String, String> flag : flags.entrySet()) {
                    System.out.println(flag.getKey() + " = " + flag.getValue()); // TODO
                }

                if (!flags.get("ANSWERED").equals("")) {
                    db.setFlags(user_id, folder_name, "answered", 1, flags.get("ANSWERED"));
                }
                if (!flags.get("DELETED").equals("")) {
                    db.setFlags(user_id, folder_name, "deleted", 1, flags.get("DELETED"));
                }
                if (!flags.get("FLAGGED").equals("")) {
                    db.setFlags(user_id, folder_name, "flagged", 1, flags.get("FLAGGED"));
                }
                if (!flags.get("DRAFT").equals("")) {
                    db.setFlags(user_id, folder_name, "draft", 1, flags.get("DRAFT"));
                }
//                if (!flags.get("RECENT").equals("")) { db.setFlags(user_id, folder_name, "recent", 1, flags.get("RECENT")); }
                if (!flags.get("UNSEEN").equals("")) {
                    db.setFlags(user_id, folder_name, "seen", 0, flags.get("UNSEEN"));
                }
//                    db.setFlags(emailAccount.getUser().getUser_id(), myFolder.getFolder_name(), "user", 1, flags.get(""));

                if (!flags.get("KEYWORD $Forwarded").equals("")) {
                    db.setFlags(user_id, folder_name, "forwarded", 1, flags.get("KEYWORD $Forwarded"));
                }
                if (!flags.get("KEYWORD $label1").equals("")) {
                    db.setFlags(user_id, folder_name, "label_1", 1, flags.get("KEYWORD $label1"));
                }
                if (!flags.get("KEYWORD $label2").equals("")) {
                    db.setFlags(user_id, folder_name, "label_2", 1, flags.get("KEYWORD $label2"));
                }
                if (!flags.get("KEYWORD $label3").equals("")) {
                    db.setFlags(user_id, folder_name, "label_3", 1, flags.get("KEYWORD $label3"));
                }
                if (!flags.get("KEYWORD $label4").equals("")) {
                    db.setFlags(user_id, folder_name, "label_4", 1, flags.get("KEYWORD $label4"));
                }
                if (!flags.get("KEYWORD $label5").equals("")) {
                    db.setFlags(user_id, folder_name, "label_5", 1, flags.get("KEYWORD $label5"));
                }
                if (!flags.get("KEYWORD $HasAttachment").equals("")) {
                    db.setFlags(user_id, folder_name, "has_attachment", 1, flags.get("KEYWORD $HasAttachment"));
                }

                //} else {
                //messages = imap_folder.getMessages();
                //}

                // 3
                long db_last_uid;

                if (check_count != part_count) {
                    db_last_uid = 1; //or 0?? to get first?
                } else {
                    db_last_uid = db.getLastUID(emailAccount.getUser().getUser_id(), imap_folder.getFullName());
                }
                IMAPMessage last_imap_message = (IMAPMessage) imap_folder.getMessage(messages_count_mail);
                long mail_last_uid = imap_folder.getUID(last_imap_message);
//            long db_last_uid   = db.getLastUID(emailAccount.getUser().getUser_id(), imap_folder.getFullName()); // TODO вынести

                if (mail_last_uid <= db_last_uid) {
                    return;
                }

                if (check_count != part_count) {
                    messages = imap_folder.getMessages();
                } else {
                    messages = imap_folder.getMessagesByUID(db_last_uid, mail_last_uid);
                }

                System.out.println("Start UID = " + db_last_uid + " END UID = " + mail_last_uid);
                System.out.println(messages.length + " �� " + imap_folder.getMessages().length);

                for (Message message : messages) {
                    if (!imap_folder.isOpen()) {
                        try {
                            imap_folder.open(IMAPFolder.READ_ONLY);
                        } catch (MessagingException e1) {
                            e1.printStackTrace();
                        }
                    }
                    db.addEmail(new Email(emailAccount.getUser(), (IMAPMessage) message, imap_folder));
                }


            }

        } catch (Exception e) {
            myFolder.setException(e);
            e.printStackTrace();
        }
    }

}