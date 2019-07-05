package com.threads;

import com.DB;
import com.Main;
import com.classes.Email;
import com.classes.MyFolder;
import com.classes.MyMessage;
import com.classes.EmailAccount;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.*;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.ArrayList;
import java.util.HashMap;

public class AddNewMessageThread implements Runnable {

    private static DB db = Main.db;

    private MyFolder     myFolder;
    private IMAPFolder   imap_folder;
    private EmailAccount emailAccount;

    private int user_id = 0;
    private String email_address = "error";
    private String folder_name   = "error";

    public AddNewMessageThread(EmailAccount emailAccount, MyFolder myFolder, IMAPFolder imap_folder) {
        if (db == null) {
            db = new DB();
        }
        this.myFolder     = myFolder;
        this.emailAccount = emailAccount;
        this.imap_folder  = imap_folder;
    }

    @Override
    public void run() {
        long messages_count_mail = 0;
        long messages_count_db   = 0;

        try {
            if (!reopenFolder("start")) {
                return;
            }  // Открыть папку на чтение если она не открыта

            addFolderListenersConnection(imap_folder);

            user_id       = emailAccount.getUser().getUser_id();
            email_address = emailAccount.getEmailAddress();
            folder_name   = imap_folder.getFullName();

            myFolder.setStatus("for start");

            for (int i = 0; i < 3; i++) {
                myFolder.setStatus("for " + i + " start");

                messages_count_mail = imap_folder.getMessageCount();
                messages_count_db   = db.getCountMessages(email_address, folder_name);

                this.myFolder.setMessages_count(messages_count_mail);
                this.myFolder.setMessages_db_count(messages_count_db);

                if (checkOldMails(user_id, email_address, folder_name, messages_count_mail, messages_count_db)) {
                    break;
                }
                myFolder.setStatus("for " + i + " end");
                break;
            }

            myFolder.setStatus("for end");

            if (messages_count_db > 0) {
                myFolder.setStatus("checkFlags start");
                checkFlags(user_id, folder_name);
                myFolder.setStatus("checkFlags end");

//                checkRemoved(user_id, folder_name, messages_count_db); // Пометить удаленные сообщения в базе
            }

            myFolder.setStatus("end_add_message_folder");

            addFolderListenersMessages(imap_folder);

            this.myFolder.setMessages_count(messages_count_mail);
            this.myFolder.setMessages_db_count(db.getCountMessages(email_address, folder_name));

            myFolder.setStatus("listening");

            int noop_sleep;

            switch (folder_name) {
//                case "INBOX": noop_sleep = 5000;  break;
                default:      noop_sleep = 40000; break;
            }

            while (!Thread.interrupted()) {
                if (reopenFolder("noop")) {
                    myFolder.updateTime_last_noop();
                }
                Thread.sleep(noop_sleep);
            }

        } catch (Exception e) {
            myFolder.setStatus("close");
            myFolder.setException(e);
            e.printStackTrace();
        } finally {
            myFolder.setStatus("close");
        }
    }

    private boolean checkOldMails(int user_id, String email_address, String folder_name, long messages_count_mail, long messages_count_db) throws MessagingException {

        myFolder.setStatus("checkOldMails start");

        Message[] messages;

        if (messages_count_db == 0) { // Если нет сообщений, то просто выйти
            if (messages_count_mail == 0) {
                return true;
            } else {
                messages = imap_folder.getMessages();
                fetchMessages(messages);
            }
        } else {
            if (messages_count_mail == 0) {
                db.deleteMessages(email_address, folder_name); // Пометить или удалить все сообщения // TODO
            } else {
                long last_uid_db   = db.getLastAddUID(emailAccount.getEmailAddress(), folder_name);
                long last_uid_mail = 0;  // imap_folder.getUID(imap_folder.getMessage(imap_folder.getMessageCount()));

                Message[] messages_tmp = imap_folder.getMessages();

                if (messages_tmp.length > 0) {
                    last_uid_mail = imap_folder.getUID(messages_tmp[messages_tmp.length - 1]);
                }

                checkRemoved(user_id, folder_name);

                if (last_uid_db > 0 && cheackRandomMessages(last_uid_db) ) {
                    if (last_uid_db < last_uid_mail) {
                        messages = imap_folder.getMessagesByUID(last_uid_db, last_uid_mail);
                        fetchMessages(messages);
                    } else if (last_uid_db == last_uid_mail) {
                        return true;
                    } else {
                        checkRemoved(user_id, folder_name);
                        return true;
                    }
                } else {
//                    db.deleteMessages(email_address, folder_name); // Пометить или удалить все сообщения // TODO
                    messages = imap_folder.getMessages();
                    fetchMessages(messages);
                }
            }
        }

        myFolder.setStatus("checkOldMails end ");

        return false;
    }

    private boolean fetchMessages(Message[] messages) throws MessagingException {

        try {
            if (!reopenFolder("fetch")) {
                Thread.sleep(10000);
            }

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE); // From, To, Cc, Bcc, ReplyTo, Subject and Date
            fp.add(FetchProfile.Item.CONTENT_INFO); // ContentType, ContentDisposition, ContentDescription, Size and LineCount
//                    fp.add(FetchProfile.Item.SIZE); // Ограничение по объему предварительно загруженных писем
//                    fp.add(FetchProfile.Item.FLAGS); //
            fp.add(UIDFolder.FetchProfileItem.UID);
            fp.add("Message-ID");
            fp.add("X-Tdfid");

            Folder tmp_folder = messages[0].getFolder();

            myFolder.setStatus("Fetch start " + messages.length + " / " + tmp_folder.getMessageCount());
            long start = System.currentTimeMillis();

            imap_folder.fetch(messages, fp);

            long finish = System.currentTimeMillis();

            myFolder.setStatus("load in DB start " + messages.length + " / " + tmp_folder.getMessageCount());

            long uid;
            Email email = null;

            int messages_count = messages.length;

            for (int i = 0; i < messages_count; i++) {

//          start = System.currentTimeMillis();

                try {
                    Message message_tmp = messages[i];

                    uid   = imap_folder.getUID(message_tmp);
                    email = new Email(user_id, email_address, messages[i], folder_name, uid, imap_folder);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("---------------------------------");
                    System.err.println(imap_folder.getFullName());
                    System.err.println(messages[0].getFolder().getFullName());
                    System.err.println("---------------------------------");
                }

                db.addEmail(email);
//                    db.updateFolderLastAddUID(email_address, email); // TODO можно упростить передав uid ^

            }

            myFolder.setStatus("load in DB end");

        } catch (Exception $e) {
            $e.printStackTrace();
        }

        return true;
    }

    private boolean reopenFolder(String reson) {

        System.out.println("reopenFolder " + reson);

        ConnectToFolder connectToFolder = null;

        try {
            int tmp_i = 0;

            while ((connectToFolder == null || !connectToFolder.is_open) && ++tmp_i <= 3) {

                connectToFolder = new ConnectToFolder(imap_folder);

                Thread connectToFolderThread = new Thread(connectToFolder);
                connectToFolderThread.setDaemon(true);
                connectToFolderThread.start();

                long start = System.currentTimeMillis();

                while (!connectToFolder.is_open && System.currentTimeMillis() < start + 20000) {
                    Thread.sleep(2000);
                }

//                connectToFolderThread.stop();

                Thread.sleep(2000);


                System.gc();

                while (connectToFolderThread.isAlive()) { // Убивать пока не умрет
                    System.out.println("one more +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//                    connectToFolderThread.stop();
                    Thread.sleep(2000);
                }


                if (connectToFolder.is_open) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connectToFolder == null) {
                return false;
            } else {
                return connectToFolder.is_open;
            }
        }
    }

    private void checkRemoved(int user_id, String folder_name) {

        try {
            // Если нет UID, то пометить как deleted
            int query_buffer = 1000;

            int messagesCount = imap_folder.getMessageCount();

            long query_count = (messagesCount / query_buffer) + 1;

            for (int j = 0; j < query_count; j++) {

                long start = (j * query_buffer + 1);
                long end = ((j + 1) * query_buffer + 1);

                if (end > messagesCount) {
                    end = messagesCount;
                }

                if (start > end) {
                    start = end;
                }

                long finalStart = start;
                long finalEnd   = end;

                if (finalStart == 0 || finalEnd == 0) {
                    return;
                }

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

//                if (finalStart == 1 &&  arr_uids.size() > 0 && (arr_uids.get(0) - 1) > 0) {
                if (finalStart == 1) {
                    db.setRemoved(user_id, folder_name, -1, arr_uids.get(0) - 1, ""); // пометить до
                }

                if (arr_uids.size() > 0) {
                    long uid_start = arr_uids.get(0);
                    long uid_end   = arr_uids.get(arr_uids.size() - 1);

                    StringBuilder str_uids = new StringBuilder(String.valueOf(arr_uids.get(0)));

                    for (int n = 1; n < arr_uids.size(); n++) {
                        str_uids.append(",").append(String.valueOf(arr_uids.get(n)));
                    }

                    db.setRemoved(user_id, folder_name, uid_start, uid_end, str_uids.toString()); //set removed flag
                }

                if (query_count == j + 1) {
                    db.setRemoved(user_id, folder_name, arr_uids.get(arr_uids.size() - 1) + 1, -1, ""); // пометить после
//                    db.updateFolderLastAddUID(email_address, user_id, folder_name, arr_uids.get(arr_uids.size() - 1)); //
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
//            emailAccount.setStatus("end_add_message_folder");
//            myFolder.setStatus("end_add_message_folder");
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
                    StringBuilder str_uids = new StringBuilder();
                    Response[] responses = imapProtocol.command("UID SEARCH " + flag.getKey(), null);
                    String[] arr_out_str = responses[0].toString().split(" ");

                    if (arr_out_str.length > 2) {
                        str_uids = new StringBuilder(arr_out_str[2]);

                        for (int n = 3; n < arr_out_str.length; n++) {
                            str_uids.append(",").append(arr_out_str[n]);
                        }
                    }

                    return str_uids.toString();
                }));
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
//            System.err.println(folder_name + " checkFlags end");
        }

    }

    private boolean cheackRandomMessages(long last_uid_db) {
        int check_count = 0;
        int sqrt  = 0;

        try {
//            System.out.println("last_uid_db = " + last_uid_db);
//            System.out.println("messages count = " + imap_folder.getMessageCount());

            Message last_message = imap_folder.getMessageByUID(last_uid_db);
            if (last_message == null) {
//                return Boolean.parseBoolean(null);
                return false;
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
                    if (myMessage.compare((IMAPMessage) message, imap_folder, true)) {
                        check_count++;
                    }
                    i++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            myFolder.setStatus("end_add_message_folder");
        }

        return (sqrt == check_count);
    }

    private void addFolderListenersConnection(IMAPFolder imap_folder) {
        imap_folder.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent connectionEvent) {
//                System.out.println("Folder opened " + imap_folder.getFullName());
                myFolder.setStatus("listening");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
//                System.out.println( "Folder disconnected " + imap_folder.getFullName());
                reopenFolder("disconnected");
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
//                System.out.println( "Folder close " + imap_folder.getFullName());
                reopenFolder("closed");
            }
        });
    }

    private void addFolderListenersMessages(IMAPFolder imap_folder) {
        imap_folder.addMessageChangedListener(messageChangedEvent -> {
            try {

                int user_id          = emailAccount.getUser().getUser_id();
                String email_address = emailAccount.getEmailAddress();
                String folder_name   = myFolder.getFolder_name();

//                myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
//                myFolder.eventCountIncriminate();

                IMAPMessage imap_message = (IMAPMessage) messageChangedEvent.getMessage();
//                    long uid = imap_folder.getUID(imap_message);
                Email email = new Email(user_id, email_address, imap_message, folder_name, 0, imap_folder);

                String message_inform = "Uid" + email.getUid() + " messageChangedEvent";
                MailingEmailAccountThread.enterMessage(email_address + " " + folder_name, message_inform);

                db.addEmail(email);

            } catch (Exception e) {
                myFolder.setStatus("error");
                myFolder.setException(e);
                e.printStackTrace();
            }
        });

        imap_folder.addMessageCountListener(new MessageCountListener() {
            @Override
            public void messagesAdded(MessageCountEvent messageCountEvent) {
                try {

                    int user_id          = emailAccount.getUser().getUser_id();
                    String email_address = emailAccount.getEmailAddress();
                    String folder_name   = myFolder.getFolder_name();

//                    myFolder.setLast_event_time(new Timestamp(new Date().getTime()));
//                    myFolder.eventCountIncriminate();
                    MailingEmailAccountThread.enterMessage(email_address + " " + folder_name,"messagesAdded");

                    for (Message message : messageCountEvent.getMessages()) {
                        Email email = new Email(user_id, email_address, message, folder_name, 0, imap_folder);

                        String message_inform = email_address + " " + folder_name + " " + email.getUid() + " messageChangedEvent";
                        MailingEmailAccountThread.enterMessage(email_address + " " + folder_name, message_inform);

                        db.addEmail(email);
//                            db.updateFolderLastAddUID(email_address, email);
                    }
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
                } finally {
                    try {
                        myFolder.setMessages_count(imap_folder.getMessageCount());
                        myFolder.setMessages_db_count(db.getCountMessages(email_address, folder_name));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent messageCountEvent) {
                //todo add removed to db TODO!!!!!
                System.out.println("messagesRemoved!!!");

//                try {
//                    for (Message message : messageCountEvent.getMessages()) {
//                        db.setDeleteFlag(user.getEmail(), imap_folder.getFullName(), message.getHeader("Message-ID")[0]);
//                        MailingEmailAccountThread.enterMessage("messagesRemoved");
//                    }
//                } catch (Exception e) {
//                    myFolder.setStatus("error");
//                    myFolder.setException(e);
//                    e.printStackTrace();
//                }
                try {
                    myFolder.setMessages_count(imap_folder.getMessageCount());
                    myFolder.setMessages_db_count(db.getCountMessages(email_address, folder_name));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

}