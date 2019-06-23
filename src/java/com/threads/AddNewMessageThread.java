package com.threads;

import com.DB;
import com.Main;
import com.classes.Email;
import com.classes.EmailAccount;
import com.classes.MyFolder;
import com.classes.MyMessage;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import javax.mail.*;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddNewMessageThread implements Runnable {

    private static DB db = Main.db;

    private MyFolder     myFolder;
    private IMAPFolder   imap_folder;
    private UIDFolder    uid_folder;
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
//        this.imap_folder  = myFolder.getImap_folder();
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

            messages_count_db = db.getCountMessages(email_address, folder_name);

            this.myFolder.setMessages_count(imap_folder.getMessageCount());
            this.myFolder.setMessages_db_count(messages_count_db);

            myFolder.setStatus("for start");

            for (int i = 0; i < 3; i++) {
                myFolder.setStatus("for " + i + " start");
//                if (!reopenFolder()) { return; }
                messages_count_mail = imap_folder.getMessageCount();
                System.out.println("messages_count_mail " + messages_count_mail);
                System.out.println("messages_count_db " + messages_count_db);

                if (checkOldMails(user_id, email_address, folder_name, messages_count_mail, messages_count_db)) {
                    break;
                }
                myFolder.setStatus("for " + i + " end");
            }

            myFolder.setStatus("for end");

            if (messages_count_db > 0) {
                setMyFolderStatus("checkFlags start");
                checkFlags(user_id, folder_name);
                setMyFolderStatus("checkFlags end");

//                System.err.println(folder_name + "cheackDelete start");
//                cheackDelete(user_id, folder_name, messages_count_db); // Пометить удаленные сообщения в базе
//                System.err.println(folder_name + " cheackDelete end");
            }

            setMyFolderStatus("end_add_message_folder");

//            System.gc();

            addFolderListenersMessages(imap_folder);

            setMyFolderStatus("listening");

            int noop_sleep = 30000;

            switch (folder_name) {
                case "INBOX": noop_sleep = 5000;  break;
                default:      noop_sleep = 30000; break;
            }

//            noop_sleep = 1000;

            while (!Thread.interrupted()) {
                reopenFolder("noop");
                Thread.sleep(noop_sleep);

//                imap_folder.doCommand(imap_protocol -> {
//                    imap_protocol.simpleCommand("NOOP", null);
//                    return imap_protocol;
//                });
            }


//            setMyFolderStatus("end_add_message_folder");

//            imap_folder.addMessageChangedListener(messageChangedEvent -> {
//                try {
//                    System.out.println("messageChangedEvent");
//                } catch (Exception e) {
//                    myFolder.setStatus("error");
//                    myFolder.setException(e);
//                    e.printStackTrace();
//                }
//            });

//            System.out.println("idle start");
//            imap_folder.idle();
//            System.out.println("idle event");

//            while (true) {
//                try {
//                    Thread.sleep(500);
//
//                    System.out.println("wait");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

        } catch (Exception e) {
            setMyFolderStatus("close");
            db.updateFolderLastException(user_id, email_address, folder_name, e.getMessage());
            myFolder.setException(e);
            e.printStackTrace();
        } finally {
            setMyFolderStatus("close");
        }
    }

    private void setMyFolderStatus(String status) {
//        System.err.println("\u001B[91m" +folder_name + " - " + status + "\u001B[0m");
        myFolder.setStatus(status);
        db.updateFolderLastStatus(user_id, email_address, folder_name, status);
    }

    private boolean checkOldMails(int user_id, String email_address, String folder_name, long messages_count_mail, long messages_count_db) throws MessagingException {

        myFolder.setStatus("checkOldMails start ");

        Message[] messages;

        System.out.println(folder_name + " messages_count_mail = " + messages_count_mail);
        System.out.println(folder_name + " messages_count_db   = " + messages_count_db);

        if (messages_count_db == 0) { // Если нет сообщений, то просто выйти
            if (messages_count_mail == 0) {
                return true;
            } else {
                System.out.println("bbb000000");
                messages = imap_folder.getMessages();
                fetchMessages(messages);
            }
        } else {
            if (messages_count_mail == 0) {
                db.deleteMessages(email_address, folder_name); // Пометить или удалить все сообщения // TODO
            } else {
                long last_uid_db   = db.getLastAddUID(user_id, emailAccount.getEmailAddress(), folder_name);
                long last_uid_mail = 0;  // imap_folder.getUID(imap_folder.getMessage(imap_folder.getMessageCount()));

                Message[] messages_tmp = imap_folder.getMessages();

                if (messages_tmp.length > 0) {
                    last_uid_mail = imap_folder.getUID(messages_tmp[messages_tmp.length - 1]);
                }

                System.out.println(folder_name + " last_uid_db   = " + last_uid_db);
                System.out.println(folder_name + " last_uid_mail = " + last_uid_mail);

                if (last_uid_db > 0 && cheackRandomMessages(last_uid_db)) {
                    if (last_uid_db < last_uid_mail) {

                        System.out.println("bbb1111");

                        messages = imap_folder.getMessagesByUID(last_uid_db, last_uid_mail);
                        fetchMessages(messages);
                    } else if (last_uid_db == last_uid_mail) {
                        System.out.println("bbb2222222");
                        return true;
                    } else {
                        System.out.println("bbb33333333");
                        cheackDelete(user_id, folder_name, messages_count_db);
                        return true;
                    }
                } else {
                    System.out.println("bbb444444");
//                    db.deleteMessages(email_address, folder_name); // Пометить или удалить все сообщения // TODO
                    messages = imap_folder.getMessages();
                    fetchMessages(messages);
                }
            }
        }

        messages = null;

        myFolder.setStatus("checkOldMails end ");

        return false;
    }

    private boolean fetchMessages(Message[] messages) throws MessagingException {

        try {
            if (!reopenFolder("fetch")) {
                Thread.sleep(100000);
            }

            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE); // From, To, Cc, Bcc, ReplyTo, Subject and Date   // 31,57 // 132 // 0+59
            fp.add(FetchProfile.Item.CONTENT_INFO); // ContentType, ContentDisposition, ContentDescription, Size and LineCount  // 206 (122 -flags) // + 91 // 0+79
//                    fp.add(FetchProfile.Item.SIZE); // Ограничение по объему предварительно загруженных писем  // count 3943
//                    fp.add(FetchProfile.Item.FLAGS); //   // 163 // +43 // 0+8
            fp.add(UIDFolder.FetchProfileItem.UID); // 0+1
            fp.add("Message-ID"); // 0+19
            fp.add("X-Tdfid"); // 0+38

            String status = "Fetch start " + messages.length + " / " + messages[0].getFolder().getMessageCount();

            System.err.println(folder_name + status);
            myFolder.setStatus(status);
            setMyFolderStatus(status);
            long start = System.currentTimeMillis();

            imap_folder.fetch(messages, fp);

            long finish = System.currentTimeMillis();
            System.err.println(folder_name + " Fetch end");
            System.err.println("Test speed fetch - " + (finish - start));

            setMyFolderStatus("load in DB start" + messages.length + " / " + messages[0].getFolder().getMessageCount());

            long uid;
            Email email = null;

            int messages_count = messages.length;

            for (int i = 0; i < messages_count; i++) {

//          start = System.currentTimeMillis();

                try {
                    Message message_tmp = messages[i];

                    uid   = imap_folder.getUID(message_tmp);
                    email = new Email(user_id, email_address, messages[i], folder_name, uid, imap_folder);

//            } catch (javax.mail.FolderClosedException e) {
//                    reopenFolder();
//                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("---------------------------------");
                    System.err.println(imap_folder.getFullName());
                    System.err.println(messages[0].getFolder().getFullName());
                    System.err.println("---------------------------------");
//                System.err.println("length = " + messages.length);
//                System.err.println("getMessageCount() = " + imap_folder.getMessageCount());
//                System.err.println("messages_count = " + messages_count);
//                System.err.println("i = " + i);
                }

//          finish = System.currentTimeMillis();
//          System.err.println("Test speed - " + (finish - start));

                if (db.addEmail(email)) {
                    db.updateFolderLastAddUID(email, email_address);
                }
            }

            setMyFolderStatus("load in DB end");

            messages = null;

        } catch (Exception $e) {
            $e.printStackTrace();
        }

        return true;
    }

    private boolean reopenFolder(String reson) {

//        if (!imap_folder.isOpen()) {
////            this.imap_folder = false;
//            try {
//                imap_folder.open(Folder.READ_ONLY);
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        System.out.println("reopen folder " + reson);
//        return true;

//        Store tmp_store = imap_folder.getStore();
        ConnectToFolder connectToFolder = null;

        try {
//            if (!tmp_store.isConnected()) {
//                tmp_store.connect();
//            }
//
//            if (!tmp_store.isConnected()) {
//                tmp_store.connect();
//                setMyFolderStatus("sleep " + reson);
//                System.err.println("=================================================");
//                System.err.println(email_address + " " + folder_name);
//                System.err.println("=================================================");
//                Thread.sleep(30000);
//            }

            int tmp_i = 0;

            while ((connectToFolder == null || !connectToFolder.is_open) && ++tmp_i <= 3) {

//                System.out.println(imap_folder.getFullName() + " - " + tmp_i);
                connectToFolder = new ConnectToFolder(imap_folder, " AddNewMessageThread -> reopenFolder <- " + reson);

                Thread connectToFolderThread = new Thread(connectToFolder);
                connectToFolderThread.setDaemon(true);
                connectToFolderThread.start();

                long start = System.currentTimeMillis();

                while (!connectToFolder.is_open && System.currentTimeMillis() < start + 10000) {
                    Thread.sleep(50);
                }

                long stop = System.currentTimeMillis();

//                System.out.println("timer " + (stop - start));

                connectToFolderThread.stop();

                if (connectToFolder.is_open) {
//            System.err.println(emailAccount.getEmailAddress() + " - " + imap_folder.getFullName() + " reOpen ok " + reson);
//           ok wssChatClient.sendText(emailAccount.getEmailAddress() + " - " + imap_folder.getFullName() + " isOpen ok");
                    continue;
                } else {
//            System.err.println(emailAccount.getEmailAddress() + " - " + imap_folder.getFullName() + " reOpen error " + reson);
//            wssChatClient.sendText(emailAccount.getEmailAddress() + " - " + imap_folder.getFullName() + " isOpen error");

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
            System.err.println(folder_name + " checkFlags start");

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

            System.err.println(folder_name + " checkFlags done");
        } catch (Exception e) {
            System.err.println(folder_name + " checkFlags error");
//            emailAccount.setException(e);
            e.printStackTrace();
        } finally {
            System.err.println(folder_name + " checkFlags end");
        }

    }

    private boolean cheackRandomMessages(long last_uid_db) {
        int check_count = 0;
        int sqrt  = 0;

        try {
            System.out.println("last_uid_db = " + last_uid_db);
            System.out.println("messages count = " + imap_folder.getMessageCount());

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

//            System.out.println("sqrt ====== " + sqrt + "  check_count ====== " + check_count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            myFolder.setStatus("end_add_message_folder");
        }


        System.out.println("------------------------+++++++++++++++++Result Random - " + (sqrt == check_count));

        return (sqrt == check_count);
    }

//    private void addMessages(Message[] messages) {
//
//        int user_id          = emailAccount.getUser().getUser_id();
//        String email_address = emailAccount.getEmailAddress();
//
//        String folder_name   = imap_folder.getFullName();
//
//        if (folder_name == null) {
//            try {
//                imap_folder.open(IMAPFolder.READ_ONLY);
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            }
//            folder_name   = imap_folder.getFullName();
//
//            if (folder_name == null) {
//                System.err.println("Не удалось подключиться к папке");
//            }
//        }
//
//        long uid;
//
//        try {
//            for (Message message : messages) {
////                if (!imap_folder.isOpen()) {
////                    try {
////                        imap_folder.open(IMAPFolder.READ_ONLY);
////                    } catch (MessagingException e) {
////                        emailAccount.setException(e);
////                        e.printStackTrace();
////                    }
////                }
//
//                uid = imap_folder.getUID(message);
//
//                Email email = new Email(user_id, email_address, message, folder_name, uid, imap_folder);
////
//                if (db.addEmail(email)) {
//                    db.updateFolderLastAddUID(email, emailAccount.getUser().getEmail());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            emailAccount.setException(e);
//        } finally {
//            myFolder.setStatus("end_add_message_folder");
//        }
//    }

    private void processFolder(final IMAPFolder folder) throws MessagingException, IOException {
//        log.fine("Processing folder " + folder.getFullName());
        System.out.println("Processing folder " + folder.getFullName());
        final long start = System.currentTimeMillis();
        if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            folder.open(Folder.READ_ONLY);
            final Message[] messages = folder.getMessages();
            System.out.println(messages.length + " messages");
            final FetchProfile fp = new FetchProfile();
            fp.add("Message-Id");
            folder.fetch(messages, fp);

            for (final Message message : messages) {


            }
        }
        final long end = System.currentTimeMillis();

        if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
            final Folder[] children=folder.list();
            if (children != null) {
                for (final Folder child : children) {
                    processFolder((IMAPFolder)child);
                }
            }
        }
    }

    private void addFolderListenersConnection(IMAPFolder imap_folder) {
        imap_folder.addConnectionListener(new ConnectionListener() {
            @Override
            public void opened(ConnectionEvent connectionEvent) {
                System.out.println("Folder opened " + imap_folder.getFullName());
                setMyFolderStatus("open");
            }

            @Override
            public void disconnected(ConnectionEvent connectionEvent) {
                System.out.println( "Folder disconnected " + imap_folder.getFullName());
                reopenFolder("disconnected");
            }

            @Override
            public void closed(ConnectionEvent connectionEvent) {
                System.out.println( "Folder close " + imap_folder.getFullName());
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

                        if (db.addEmail(email)) {
                            db.updateFolderLastAddUID(email, email_address);
                        }
                    }
                } catch (Exception e) {
                    myFolder.setStatus("error");
                    myFolder.setException(e);
                    e.printStackTrace();
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
            }
        });
    }

}