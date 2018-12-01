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

    private static DB db;

    private MyFolder     myFolder;
    private IMAPFolder   imap_folder;
    private EmailAccount emailAccount;

    public AddNewMessageThread(EmailAccount emailAccount, MyFolder myFolder) {
        if (db == null) {
            db = new DB();
        }
        this.myFolder     = myFolder;
        this.emailAccount = emailAccount;
        this.imap_folder  = myFolder.getImap_folder();
    }

    @Override
    public void run() {
        try {
            long messages_count_mail = imap_folder.getMessageCount();
            long messages_count_db = db.getCountMessages(emailAccount.getUser().getEmail(), myFolder.getFolder_name());
            System.err.println("messages_count_db = " + messages_count_db);

            int user_id = emailAccount.getUser().getUser_id();
            String folder_name = myFolder.getFolder_name();

            System.out.println("messages_count_mail = " + messages_count_mail);
            System.out.println("messages_count_db   = " + messages_count_db);

            Message[] messages;

            if (messages_count_db == 0) { // Если нет сообщений, то просто выйти
                if (messages_count_mail == 0) {
                    return;
                } else {
                    messages = imap_folder.getMessages();
                    addMessages(messages);
                }
            } else {
                if (messages_count_mail == 0) {
                    System.out.println("delete mc = 0");
                    cheackDelete(user_id, folder_name, messages_count_db); // Пометить удаленные сообщения в базе
//                    db.deleteMessages(emailAccount.getEmailAddress(), folder_name); // Пометить или удалить все сообщения // TODO
                } else {
//                    long  messages_last_UID_db = db.getCountMessages(emailAccount.getUser().getUser_id(), myFolder.getFolder_name());
                    long last_uid_db   = db.getLastAddUID(user_id, emailAccount.getEmailAddress(), folder_name);
                    long last_uid_mail = imap_folder.LASTUID; // imap_folder.getUID(imap_folder.getMessage(imap_folder.getMessageCount()));

                    if (last_uid_db > 0 && cheackRandomMessages(last_uid_db)) {
                        checkFlags(user_id, folder_name); // Обновить флаги
                        messages = imap_folder.getMessagesByUID(last_uid_db, last_uid_mail);
                        addMessages(messages);
                    } else {
                        System.out.println("last_uid_db = 0 " + last_uid_db);
//                        System.out.println("random =  " + cheackRandomMessages(last_uid_db));
                        System.out.println("delete");
//                        System.exit(0);
//                        db.deleteMessages(emailAccount.getEmailAddress(), folder_name); // Пометить или удалить все сообщения // TODO
                        cheackDelete(user_id, folder_name, messages_count_db); // Пометить удаленные сообщения в базе
                        messages = imap_folder.getMessages();
                        addMessages(messages);
                    }
                }

                cheackDelete(user_id, folder_name, messages_count_db); // Пометить удаленные сообщения в базе
            }

        } catch (Exception e) {
            myFolder.setException(e);
            e.printStackTrace();
        } finally {
//            emailAccount.setStatus("end_add_message_folder");
            myFolder.setStatus("end_add_message_folder");
        }
    }

    private void cheackDelete(int user_id, String folder_name, long messages_count_db) {
        try {
            // Если нет UID, то пометить как deleted
            int query_buffer = 1000;
            long query_count = (messages_count_db / query_buffer) + 1;

            for (int j = 0; j < query_count; j++) {

                long start = (j * query_buffer + 1);
                long end = ((j + 1) * query_buffer + 1);

                if (end > messages_count_db) {
                    end = messages_count_db;
                }
                if (start > end) {
                    start = end;
                }

                long finalEnd   = end;
                long finalStart = start;

                if (finalStart == 0 || finalEnd == 0) {
                    return;
                }

                ArrayList<Long> arr_uids;

                    arr_uids = (ArrayList<Long>) imap_folder.doCommand(imapProtocol -> {
                        Response[] responses;
                        ArrayList<Long> arr_uids_tmp = new ArrayList<>();

                        System.out.println("finalStart - " + finalStart);
                        System.out.println("finalEnd - " + finalEnd);
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
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
//            emailAccount.setStatus("end_add_message_folder");
            myFolder.setStatus("end_add_message_folder");
        }
    }

    private void checkFlags(int user_id, String folder_name) {
        try {

        db.setFlags(emailAccount.getUser().getUser_id(), imap_folder.getFullName()); // Обнулить флаги

        HashMap<String, String> flags = new HashMap<>();

        flags.put("KEYWORD $HasAttachment", null);
        flags.put("KEYWORD $Forwarded", null);
        flags.put("KEYWORD $label1", null);
        flags.put("KEYWORD $label2", null);
        flags.put("KEYWORD $label3", null);
        flags.put("KEYWORD $label4", null);
        flags.put("KEYWORD $label5", null);
        flags.put("FLAGGED",  null);
        flags.put("ANSWERED", null);
        flags.put("DELETED",  null);
        flags.put("DRAFT",    null);
        flags.put("UNSEEN",   null);

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

//        for (HashMap.Entry<String, String> flag : flags.entrySet()) {
//            System.out.println(flag.getKey() + " = " + flag.getValue()); // TODO
//        }

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
        if (!flags.get("UNSEEN").equals("")) {
            db.setFlags(user_id, folder_name, "seen", 0, flags.get("UNSEEN"));
        }
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

        } catch (Exception e) {
            emailAccount.setException(e);
            e.printStackTrace();
        } finally {
            myFolder.setStatus("end_add_message_folder");
        }

    }

//    private boolean cheackRandomMessages(int messages_count_db) {
//        int check_count = 0;
//        int part_count  = 0;
//
//        try {
//            part_count = (int) Math.ceil(Math.sqrt(messages_count_db) / 2); // Корень квадратный /2
//
//            // Рандомные сообщения из базы
//            ArrayList<MyMessage> myMessages = db.getRandomMessages(
//                    emailAccount.getUser().getUser_id(),
//                    myFolder.getFolder_name(),
//                    part_count
//            );
//            long[] uids = new long[part_count];
//            int i = 0;
//
//            for (MyMessage myMessage : myMessages) {
//                if (myMessage.getUid() > 0) {
//                    uids[i++] = myMessage.getUid();
//                }
//            }
//
//            Message[] messages_tmp = new Message[0];
//
//            if (messages_count_db > 0) {
//                messages_tmp = imap_folder.getMessagesByUID(uids);
//            }
//
//            i = 0;
//
//            for (MyMessage myMessage : myMessages) {
//                if (myMessage.compare((IMAPMessage) messages_tmp[i++], imap_folder)) {
//                    check_count++;
//                }
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } finally {
//            myFolder.setStatus("end_add_message_folder");
//        }
//
//        System.out.println("part_count" + part_count + "check_count" + check_count);
//
//        return (part_count == check_count);
//    }

    private boolean cheackRandomMessages(long last_uid_db) {
        int check_count = 0;
        int sqrt  = 0;

        try {
            System.out.println("last_uid_db = " + last_uid_db);
            System.out.println("messages count = " + imap_folder.getMessageCount());

            Message last_message = imap_folder.getMessageByUID(last_uid_db);
            if (last_message == null) {
                return Boolean.parseBoolean(null);
            }
            int last_id = last_message.getMessageNumber();

            sqrt = (int) Math.ceil(Math.sqrt(last_id) / 2); // Корень квадратный /2

            int i = 1;

            while (i <= sqrt) {
                Message message = imap_folder.getMessage(1 + (int) (Math.random() * sqrt));
                long uid = imap_folder.getUID(message);
                MyMessage myMessage = db.getMyMessage(emailAccount.getEmailAddress(), imap_folder.getFullName(), uid);

                if (myMessage == null) {
                    continue;
                } else {
                    if (myMessage.compare((IMAPMessage) message, imap_folder, false)) {
                        check_count++;
                    }
                    i++;
                }
            }

//            System.out.println("sqrt ====== " + sqrt + "  check_count ====== " + check_count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myFolder.setStatus("end_add_message_folder");
        }

        return (sqrt == check_count);
    }

    private void addMessages(Message[] messages) {
        try {

            for (Message message : messages) {
                if (!imap_folder.isOpen()) {
                    try {
                        imap_folder.open(IMAPFolder.READ_ONLY);
                    } catch (MessagingException e) {
                        emailAccount.setException(e);
                        e.printStackTrace();
                    }
                }

                Email email = new Email(emailAccount.getUser(), message, imap_folder);

                if (db.addEmail(email)) {
                    db.updateFolderLastAddUID(email, emailAccount.getUser().getEmail());
                }
            }
        } catch (Exception e) {
            emailAccount.setException(e);
        } finally {
            myFolder.setStatus("end_add_message_folder");
        }
    }

}