package com;

import com.classes.Email;
import com.classes.MyMessage;
import com.classes.User;
import com.service.SettingsMail;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
public class DB implements AutoCloseable {

//    private static final String[] params = {
//            "useSSL=false",
//            "serverTimezone=UTC"
//
//    };

    private static final String[] params = {
                                               "useSSL=false",
                                               "useUnicode=true",
                                               "characterEncoding=utf-8"
                                           };
    private static Connection        con;
    private static Statement         stmt;
    private static PreparedStatement prep_stmt;
    private static ResultSet         rs;
    private ResultSetMetaData rsmd;
    private static boolean is_line = false;
    private static int count_errors = 0;
    public static boolean result = false;

    String USER;
    String PASSWORD;
    String HOST;
    String PORT;
    String SCHEMA;
    String URL;

    public DB() {
        new SettingsMail();

        USER     = SettingsMail.getUser();
        PASSWORD = SettingsMail.getPassword();
        HOST     = SettingsMail.getHost();
        PORT     = SettingsMail.getPort();
        SCHEMA   = SettingsMail.getSchema();
        URL      = "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA;

//        params[0] = "useSSL="            + SettingsMail.getUsessl();
//        params[1] = "useUnicode="        + SettingsMail.getUseunicode();
//        params[2] = "characterEncoding=" + SettingsMail.getCharacterencoding();

        connectToDB();
    }

    public boolean connectToDB() {

        boolean result = true;

        try {
//            System.out.println("Переподключение к БД");

            Class.forName("com.mysql.jdbc.Driver"); // MySQL 5
//            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8
            con = DriverManager.getConnection(URL + "?" + arrayToString(params, "&"), USER, PASSWORD); // JDBC подключение к MySQL
//            con = DriverManager.getConnection(URL, USER, PASSWORD); // JDBC подключение к MySQL

            if (con == null) {                              // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                result = false;
//                System.exit(0);                          // И выйти из программы
            }

            stmt = con.createStatement(); // getting Statement object to execute query
        } catch(Exception e) {
            result = false;
            e.printStackTrace();

            return false;
        } finally {
            DB.result = result;
            return result;
        }
    }

    public boolean addEmail(Email email) {

        if (email.getFolder() == null) {
            return false;
        }

        String query = "" +
            "INSERT INTO `a_api_emails`(\n" +
            "    `direction`, \n"   + // 1
            "    `user_id`, \n"     + // 2
            "    `client_id`, \n"   + // 3
            "    `uid`, \n"         + // 4
            "    `message_id`, \n"  + // 5
            "    `from`, \n"        + // 6
            "    `to`, \n"          + // 7
            "    `in_reply_to`, \n" + // 8
            "    `references`, \n"  + // 9
            "    `message_date`, \n"+ //10
            "    `size`, \n"        + //11
            "    `subject`, \n"     + //12
            "    `folder`, \n"      + //13
            "    `flagged`, \n"     + //14
            "    `answered`, \n"    + //15
            "    `deleted`, \n"     + //16
            "    `seen`, \n"        + //17
            "    `draft`, \n"       + //18
            "    `forwarded`, \n"   + //19
            "    `label_1`, \n"      + //20
            "    `label_2`, \n"      + //21
            "    `label_3`, \n"      + //22
            "    `label_4`, \n"      + //23
            "    `label_5`, \n"      + //24
            "    `has_attachment`, \n" + //25
            "    `time`, \n"         + //26
            "    `email_account`, \n" + //27
            "    `tdf_id` \n"        + //28
            ") \n"                 +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n" +
            "    ON DUPLICATE KEY UPDATE \n" +
            "        `direction`      = VALUES(`direction`), \n" +
            "        `user_id`        = VALUES(`user_id`), \n" +
            "        `client_id`      = VALUES(`client_id`), \n" +
            "        `message_id`     = VALUES(`message_id`), \n" +
            "        `from`           = VALUES(`from`), \n" +
            "        `to`             = VALUES(`to`), \n" +
            "        `in_reply_to`    = VALUES(`in_reply_to`), \n" +
            "        `references`     = VALUES(`references`), \n" +
            "        `message_date`   = VALUES(`message_date`), \n" +
            "        `size`           = VALUES(`size`), \n" +
            "        `subject`        = VALUES(`subject`), \n" +
            "        `flagged`        = VALUES(`flagged`), \n" +
            "        `answered`       = VALUES(`answered`), \n" +
            "        `deleted`        = VALUES(`deleted`), \n" +
            "        `seen`           = VALUES(`seen`), \n" +
            "        `draft`          = VALUES(`draft`), \n" +
            "        `forwarded`      = VALUES(`forwarded`), \n" +
            "        `label_1`        = VALUES(`label_1`), \n" +
            "        `label_2`        = VALUES(`label_2`), \n" +
            "        `label_3`        = VALUES(`label_3`), \n" +
            "        `label_4`        = VALUES(`label_4`), \n" +
            "        `label_5`        = VALUES(`label_5`), \n" +
            "        `has_attachment` = VALUES(`has_attachment`), \n" +
            "        `time`           = VALUES(`time`), \n" +
            "        `tdf_id`         = VALUES(`tdf_id`);";

        try {
            is_line = true;
            PreparedStatement prepare_statement_tmp = con.prepareStatement(query);

            prepare_statement_tmp.setString(1, email.getDirection());
            prepare_statement_tmp.setInt(2, email.getUser_id());
            prepare_statement_tmp.setInt(3, email.getClient_id());
            prepare_statement_tmp.setLong(4, email.getUid());
            prepare_statement_tmp.setString(5, email.getMessage_id());
            prepare_statement_tmp.setString(6, email.getFrom());
            prepare_statement_tmp.setString(7, email.getTo());
            prepare_statement_tmp.setString(8, email.getIn_replay_to());
            prepare_statement_tmp.setString(9, email.getReferences());
            prepare_statement_tmp.setTimestamp(10, email.getDate());
            prepare_statement_tmp.setLong(11, email.getSize());
            prepare_statement_tmp.setString(12, email.getSubject());
            prepare_statement_tmp.setString(13, email.getFolder());
            prepare_statement_tmp.setInt(14, email.getFlagged());
            prepare_statement_tmp.setInt(15, email.getAnswred());
            prepare_statement_tmp.setInt(16, email.getDeleted());
            prepare_statement_tmp.setInt(17, email.getSeen());
            prepare_statement_tmp.setInt(18, email.getDraft());
            prepare_statement_tmp.setInt(19, email.getForwarded());
            prepare_statement_tmp.setInt(20, email.getLabel1());
            prepare_statement_tmp.setInt(21, email.getLabel2());
            prepare_statement_tmp.setInt(22, email.getLabel3());
            prepare_statement_tmp.setInt(23, email.getLabel4());
            prepare_statement_tmp.setInt(24, email.getLabel5());
            prepare_statement_tmp.setInt(25, email.getHas_attachment());
            prepare_statement_tmp.setTimestamp(26, email.getUpdate());
            prepare_statement_tmp.setString(27, email.getEmail_account());
            prepare_statement_tmp.setString(28, email.getTdf_id());
            prepare_statement_tmp.executeUpdate();

        } catch (
            com.mysql.jdbc.exceptions.jdbc4.CommunicationsException |
            com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e
        ) {

            if (count_errors > 10) {
                return false;
            }

            if (connectToDB()) {
                return addEmail(email);
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("addEmail error");

            e.printStackTrace();
            return addEmail(email);
        } finally {
            count_errors = 0;
            is_line = false;
        }

        return true;
    }

    public ArrayList<User> getUsers() {

        String query = "" +
            "SELECT " +
            "    `users`.`id`, "            +
            "    `users`.`user_id`, "       +
            "    `users`.`email`, "         +
            "    `users`.`password`, "      +
            "    `users`.`is_monitoring`, " +
            "    `users`.`is_default`, "    +
            "    `settings`.`host`, "       +
            "    `settings`.`port`, "       +
            "    `users`.`login`, "         +
            "    `users`.`name_from`, "     +
            "    `settings`.`charset`, "    +
            "    `settings`.`secure`, "     +
            "    `users`.`success` "        +
            "FROM `" + SettingsMail.getTable_users() + "` AS `users` " +
            "INNER JOIN `a_my_emails_settings` AS `settings` " +
            "   ON " +
            "       `users`.`email_provider` = `settings`.`provider` AND " +
            "       `settings`.`type` = 'imap'  " +
            "WHERE " +
//                "   `user_id` = 320 AND " +
//                "   `user_id` = 25 AND " +
                "   `is_monitoring` = 1 " +
            "ORDER  BY `users`.`email`;"
//                +
//                "`success` = 1"
                ;

        ArrayList<User> users = new ArrayList<>();

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                users.add(
                    new User(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getBoolean(5),
                        rs.getBoolean(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getInt(13)
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return users;
    }

    public long getLastUID(String account_email, String folder_name) {

        String query = "" +
                "SELECT MAX(`uid`) "            +
                "FROM `a_api_email_folders` " +
                "WHERE " +
                "    `account_email` = '"+account_email+"' AND " +
                "    `folder` = '"+folder_name+"' ";

        long last_uid = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs == null) { return 0; }
            if (rs.next()) { last_uid = rs.getLong(1); }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            is_line = false;
        }

        return last_uid;
    }

    public long getCountMessages(String email_account, String folder_name) {
//        System.out.println("getCountMessages start");
//        System.out.println("getCountMessages 1");
        String query = "" +
                "SELECT COUNT(`uid`) "            +
                "FROM `a_api_emails` " +
                "WHERE " +
                "    `email_account` = \"" + email_account + "\" AND " +
                "    `folder`        = \"" + folder_name   + "\" AND " +
                "    `removed`       = \"0\";" ;

        long count_messages = 0;

//        System.out.println("getCountMessages 2");
        try {

            if (con.isClosed()) {
                connectToDB();
            }

            if (is_line) {
//                System.out.println("getCountMessages 3");
                Thread.sleep(100);
            }
            is_line = true;

//            System.out.println("getCountMessages 5");

            stmt = con.createStatement();
//            System.out.println("getCountMessages 6");
            ResultSet rs_tmp = stmt.executeQuery(query);
//            System.out.println("getCountMessages 7");

            if (rs_tmp.next()) {
//                System.out.println("getCountMessages 8");
                count_messages = rs_tmp.getLong(1);
            }

//            System.out.println("getCountMessages 9");
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
            connectToDB();
            e.printStackTrace();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return getCountMessages(email_account, folder_name);
        } catch (Exception e) {
            e.printStackTrace();
            return getCountMessages(email_account, folder_name);
        } finally {
            is_line = false;
        }

        return count_messages;
    }

    public int updateSuccess(String email_address, int success) {

        String query = "" +
                "UPDATE `a_my_users_emails` " +
                "SET " +
                "    `success` = '" + success + "' " +
                "WHERE" +
                "    `email`     = '" + email_address + "';";

        int result = 0;

        try {
            if (con.isClosed()) {
                connectToDB();
            }

            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
            connectToDB();
            if (++count_errors > 10) {
                e.printStackTrace();
            } else {
                updateSuccess(email_address, success);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            count_errors = 0;
            is_line = false;
        }

        return result;
    }

    public int cleanStatus() {

        String query = "" +
                "UPDATE `a_api_email_folders` " +
                "SET " +
                "    `exception` = NULL, " +
                "    `status`    = NULL ;";

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return result;
    }

    public int changeFolderName(Email email, String new_folder_name) { // TODO изменить у сообщений имя папки (проверить)

        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET " +
                "    `folder` = '" + new_folder_name   + "', " +
                "    `time`   = '" + email.getUpdate() + "'  " +
                "WHERE" +
                "    `folder`     = '" + email.getFolder() + "' AND " +
                "    `message_id` = '" + email.getMessage_id() + "';";

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return result;
    }

    @Override
    public void close() {
        try {
            assert con  != null; if (con  != null) con.close();
            assert stmt != null; if (stmt != null) stmt.close();
            assert rs   != null; if (rs   != null) rs.close();
        } catch(SQLException ignored) { }
    }

    public String arrayToString(String[] arr, String symbol) {

        StringBuffer str = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i]);

            if (i != (arr.length - 1)) {
                str.append(symbol);
            }
        }

        return str.toString();
    }

    public int setDeleteFlag(String email_address, String folder_name, String message_id) { // TODO изменение флага сообщенией на удаленное (проверить)

        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET " +
                "    `deleted` = 1, " +
                "    `time`  = '" + new Timestamp(new Date().getTime()) + "'  " +
                "WHERE" +
                "    `email_account` = '" + email_address + "' AND " +
                "    `folder` = '" + folder_name + "' AND " +
                "    `message_id` = '" + folder_name + "';";

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            System.err.println(query);
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return result;
    }

    public int setFlags(int user_id, String folder_name) {
        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET " +
                "    `flagged`        = 0, " +
                "    `answered`       = 0, " +
                "    `deleted`        = 0, " +
                "    `seen`           = 1, " +
                "    `draft`          = 0, " +
                "    `forwarded`      = 0, " +
                "    `label_1`        = 0, " +
                "    `label_2`        = 0, " +
                "    `label_3`        = 0, " +
                "    `label_4`        = 0, " +
                "    `label_5`        = 0, " +
                "    `has_attachment` = 0  " +
                "WHERE TRUE ";
        if (user_id != 0) {
            query += String.format(" AND `user_id` = '%d' ", user_id);
        }
        if (folder_name != null) {
            query += String.format(" AND `folder` = '%s' ", folder_name);
        }

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return result;
    }

    public int setFlags(int user_id, String folder_name, String flag_name, byte flag_value) {
        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET `"+flag_name+"` = "+flag_value+" " +
                "WHERE  " +
                "   `user_id` = '"+user_id+"' AND" +
                "   `folder` = '"+flag_name+"';";

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return result;
    }

    public int setFlags(int user_id, String folder_name, String flag_name, int flag_value, String uids) {
        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET `"+flag_name+"` = "+flag_value+" " +
                "WHERE  " +
                "   `user_id` = '"+user_id+"' AND" +
                "   `folder` = '"+folder_name+"' AND " +
                "   `uid` IN ("+uids+");";

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
//            System.out.println("====================================================");
//            System.out.println(query);
//            System.out.println("====================================================");
        } catch (Exception e) {
            e.printStackTrace();
            return setFlags(user_id, folder_name, flag_name, flag_value, uids);
        } finally {
            is_line = false;
        }

        return result;
    }

    public ArrayList<MyMessage> getRandomMessages(int user_id, String folder_name, int count) { // TODO не используется

        String query = "" +
                "SELECT" +
                "    `direction`, "      + //1
                "    `user_id`, "        + //2
                "    `client_id`, "      + //3
                "    `uid`,"             + //4
                "    `message_id`, "     + //5
                "    `from`, "           + //6
                "    `to`, "             + //7
                "    `in_reply_to`, "    + //8
                "    `references`, "     + //9
                "    `message_date`, "   + //10
                "    `size`, "           + //11
                "    `subject`, "        + //12
                "    `folder`, "         + //13

                "    `recent`, "        + //14
                "    `flagged`, "        + //14
                "    `answered`, "       + //15
                "    `deleted`, "        + //16
                "    `seen`, "           + //17
                "    `draft`, "          + //18

                "    `forwarded`, "      + //19
                "    `label_1`, "        + //20
                "    `label_2`, "        + //21
                "    `label_3`, "        + //22
                "    `label_4`, "        + //23
                "    `label_5`, "        + //24
                "    `has_attachment`, " + //25

                "    `time`, "           +  //26

                "    `email_account` "   +  //27

                "FROM `a_api_emails` " +
                "WHERE " +
                "    `user_id` = '"+user_id +"' AND " +
                "    `folder` = '"+folder_name+"' " +
                "ORDER BY RAND() LIMIT "+count+";"; // TODO email_acc

        ArrayList<MyMessage> myMessages = new ArrayList<>();

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                myMessages.add(
                    new MyMessage(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getLong(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getTimestamp(10),
                        rs.getLong(11),
                        rs.getString(12),
                        rs.getString(13),

                        rs.getInt(14),
                        rs.getInt(15),
                        rs.getInt(16),
                        rs.getInt(17),
                        rs.getInt(18),

                        rs.getInt(19),
                        rs.getInt(20),
                        rs.getInt(21),
                        rs.getInt(22),
                        rs.getInt(23),
                        rs.getInt(24),
                        rs.getInt(25),

                        rs.getTimestamp(26),
                        rs.getString(27)
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return myMessages;
    }

    public int setRemoved(int user_id, String folder_name, long uid_start, long uid_end, String uids) {

        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET `removed` = 1, " +
                "    `time` = '" + new java.sql.Timestamp(new Date().getTime()) + "' " +
                "WHERE  " +
                "   `folder` = '" + folder_name + "' AND `user_id` = '" + user_id + "' ";

        if (uid_start > -1) {
            query += " AND `uid` >= '"+uid_start + "' ";
        }

        if (uid_end > -1) {
            query += " AND `uid` <= '"+uid_end+"' ";
        }

        if (!uids.equals("")) {
            query += " AND  `uid` NOT IN ("+uids+") ";
        }

//        if (folder_name.equals("Спам")) {
//            System.err.println(query);
//        }

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return result;
    }

    public int deleteMessage(int user_id, String folder_name, long uid) {
        String query = "" +
                "DELETE `a_api_emails` " +
                "SET `deleted` = 1, " +
                "    `time` = '" + new java.sql.Timestamp(new Date().getTime()) + "' " +
                "WHERE  `user_id` = '"+user_id+"' AND " +
                "       `folder` = '"+folder_name+"' AND " +
                "       `uid` = '"+uid+"'";

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return result;
    }

    public boolean updateFolderLastAddUID(Email email, String email_address) {

        String query = "" +
            "INSERT INTO `a_api_email_folders`(" +
            "    `user_id`,        " +
            "    `account_email`,  " +
            "    `folder_name`,    " +
            "    `last_add_uid`   " +
            ") VALUES ( " +
            " "  + email.getUser_id() + ", "  +
            " '" + email_address      + "', " +
            " '" + email.getFolder()  + "', " +
            " "  + email.getUid()     + " "  +
            ") ON DUPLICATE KEY UPDATE" +
            " `last_add_uid` = VALUES(`last_add_uid`);";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return true;
    }

    public boolean updateFolderLastException(int user_id, String email_address, String folder_name, String exception_text) {

        String query = "" +
                "INSERT INTO `a_api_email_folders`(" +
                "    `user_id`,        " +
                "    `account_email`,  " +
                "    `folder_name`,    " +
                "    `exception`     " +
                ") VALUES ( " +
                " "  + user_id + ", "  +
                " '" + email_address      + "', " +
                " '" + folder_name  + "', " +
                " '" + exception_text + "' "  +
                ") ON DUPLICATE KEY UPDATE" +
                " `exception` = VALUES(`exception`);";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.err.println("query======================================================================");
            System.err.println(query);
            System.err.println("query======================================================================");
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return true;
    }

    public boolean updateFolderLastStatus(int user_id, String email_address, String folder_name, String status) {

        String query = "" +
                "INSERT INTO `a_api_email_folders`(" +
                "    `user_id`,        " +
                "    `account_email`,  " +
                "    `folder_name`,    " +
                "    `status`     " +
                ") VALUES ( " +
                " "  + user_id + ", "  +
                " '" + email_address      + "', " +
                " '" + folder_name  + "', " +
                " '" + status + "' "  +
                ") ON DUPLICATE KEY UPDATE" +
                " `status` = VALUES(`status`);";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt.executeUpdate(query);
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
            connectToDB();
            updateFolderLastStatus(user_id, email_address, folder_name, status);
        } catch (Exception e) {
            System.err.println("query======================================================================");
            System.err.println(query);
            System.err.println("query======================================================================");
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return true;
    }

    public boolean updateAccountStatus(int id,  String status) {
        return updateQuery(id, "status", status);
    }

    public boolean updateAccountError(int id,  String error_text) {

        return updateQuery(id, "error", error_text);
    }

    public boolean updateQuery(int id, String key, String value) {
        String query = "" +
                "UPDATE `a_my_users_emails` " +
                "SET `" + key + "` = '" + value + "' " +
                "WHERE `id` = " + id + ";";

        return updateQuery(query);
    }

    public boolean updateQuery(String query) {
        try {
            if (con.isClosed()) {
                this.connectToDB();
            }

            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt.executeUpdate(query);
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
            connectToDB();
            updateQuery(query);
        } catch (Exception e) {
            System.err.println("query======================================================================");
            System.err.println(query);
            System.err.println("query======================================================================");
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return true;
    }

//    public boolean updateFolderLastStatus(int user_id, String email_address, String folder_name, String status) {
//
//        String query = "" +
//                "INSERT INTO `a_api_email_folders`(" +
//                "    `user_id`,        " +
//                "    `account_email`,  " +
//                "    `folder_name`,    " +
//                "    `status`     " +
//                ") VALUES ( " +
//                " "  + user_id + ", "  +
//                " '" + email_address      + "', " +
//                " '" + folder_name  + "', " +
//                " '" + message_count_db + "', "  +
//                " '" + message_count_mail + "' "  +
//                ") ON DUPLICATE KEY UPDATE" +
//                " `message_count_db`   = VALUES(`message_count_db`),";
//                " `message_count_mail` = VALUES(`message_count_mail`);";
//        try {
//            if (is_line) {
//                Thread.sleep(100);
//            }
//            is_line = true;
//
//            stmt.executeUpdate(query);
//        } catch (Exception e) {
//            System.err.println("query======================================================================");
//            System.err.println(query);
//            System.err.println("query======================================================================");
//            e.printStackTrace();
//        } finally {
//            is_line = false;
//        }
//
//        return true;
//    }

    public long getLastAddUID(int user_id, String account_email, String folder_name) {
        long last_add_uid = 0;

        String query = "" +
                "SELECT `last_add_uid` " +
                "FROM `a_api_email_folders` " +
                "WHERE " +
                "    `user_id`       = '" + user_id       + "' AND " +
                "    `account_email` = '" + account_email + "' AND " +
                "    `folder_name`   = '" + folder_name   + "' ;";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            if (rs_tmp == null) {
                return 0;
            }
            if (rs_tmp.next()) {
                last_add_uid = rs_tmp.getLong(1);
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(query);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return getLastAddUID(user_id, account_email, folder_name);
        } finally {
            is_line = false;
        }

        return last_add_uid;
    }

    public boolean updateFolderLastEventUID(Email email, String email_address) {
        String query = "" +
            "INSERT INTO `a_api_email_folders`(" +
            "    `user_id`,        " +
            "    `account_email`,  " +
            "    `folder_name`,    " +
            "    `last_event_uid`, " +
            "    `update_time`     " +
            ") VALUES ( " +
            " "  + email.getUser_id() + ", "  +
            " '" + email_address      + "', " +
            " '" + email.getFolder()  + "', " +
            " "  + email.getUid()     + ", "  +
            " '" + new Timestamp(new Date().getTime()) + "' " +
            ") ON DUPLICATE KEY UPDATE" +
            " `last_event_uid` = VALUES(`last_event_uid`);";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return true;
    }

    public long getLastEventUID(int user_id, String account_email, String folder_name) {
        long last_event_uid = 0;

        String query = "" +
            "SELECT `last_event_uid` " +
            "FROM `a_api_email_folders` " +
            "WHERE " +
            "    `user_id` = '"+user_id +"' AND " +
            "    `account_email` = '"+account_email +"' AND " +
            "    `folder` = '"+folder_name+"' ;";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                last_event_uid = rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return last_event_uid;
    }

    public boolean deleteMessages(String email_address, String folder_name) {
        String query = "" +
                "DELETE FROM `a_api_emails` " +
                "WHERE" +
                "    `email_account` = '" + email_address + "' AND " +
                "    `folder` = '" + folder_name + "';";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return true;
    }

    public MyMessage getMyMessage(String email_address, String folder_name, long uid) {
        String query = "" +
            "SELECT \n" +
            "    `direction`, \n"      + //1
            "    `user_id`, \n"        + //2
            "    `client_id`, \n"      + //3
            "    `uid`, \n"            + //4
            "    `message_id`, \n"     + //5
            "    `from`, \n"           + //6
            "    `to`, \n"             + //7
            "    `in_reply_to`, \n"    + //8
            "    `references`, \n"     + //9
            "    `message_date`, \n"   + //10
            "    `size`, \n"           + //11
            "    `subject`, \n"        + //12
            "    `folder`, \n"         + //13

            "    `flagged`, \n"        + //14
            "    `answered`, \n"       + //15
            "    `deleted`, \n"        + //16
            "    `seen`, \n"           + //17
            "    `draft`, \n"          + //18

            "    `forwarded`, \n"      + //19
            "    `label_1`, \n"        + //20
            "    `label_2`, \n"        + //21
            "    `label_3`, \n"        + //22
            "    `label_4`, \n"        + //23
            "    `label_5`, \n"        + //24
            "    `has_attachment`, \n" + //25

            "    `time`, \n"           + //26

            "    `email_account` \n"   + //27
            "FROM `a_api_emails` \n" +
            "WHERE \n" +
            "    `email_account` = '" + email_address + "' AND \n" +
            "    `uid` = '" + uid + "' AND \n" +
            "    `folder` = '" + folder_name + "';";

        try {
            while (is_line) {
//            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            if (rs_tmp.next()) {
                return new MyMessage(
                    rs_tmp.getString("direction"),
                    rs_tmp.getInt("user_id"),
                    rs_tmp.getInt("client_id"),
                    rs_tmp.getLong("uid"),
                    rs_tmp.getString("message_id"),
                    rs_tmp.getString("from"),
                    rs_tmp.getString("to"),
                    rs_tmp.getString("in_reply_to"),
                    rs_tmp.getString("references"),
                    rs_tmp.getTimestamp("message_date"),
                    rs_tmp.getLong("size"),
                    rs_tmp.getString("subject"),
                    rs_tmp.getString("folder"),
                    rs_tmp.getInt("flagged"),
                    rs_tmp.getInt("answered"),
                    rs_tmp.getInt("deleted"),
                    rs_tmp.getInt("seen"),
                    rs_tmp.getInt("draft"),
                    rs_tmp.getInt("forwarded"),
                    rs_tmp.getInt("label_1"),
                    rs_tmp.getInt("label_2"),
                    rs_tmp.getInt("label_3"),
                    rs_tmp.getInt("label_4"),
                    rs_tmp.getInt("label_5"),
                    rs_tmp.getInt("has_attachment"),
                    rs_tmp.getTimestamp("time"),
                    rs_tmp.getString("email_account")
                );
            }
        } catch (Exception e) {
//            System.err.println("======================================");
//            System.err.println(query);
//            System.err.println("======================================");
//            e.printStackTrace();
            return getMyMessage(email_address, folder_name, uid);
        } finally {
            is_line = false;
        }

        return null;
    }

    public ArrayList<User> getUsersUpdate() {

        String query = "" +
                "SELECT " +
                "    `id`, "            +
                "    `user_id`, "       +
                "    `email`, "         +
                "    `password`, "      +
                "    `is_monitoring`, " +
                "    `is_default`, "    +
                "    `host`, "          +
                "    `port`, "          +
                "    `login`, "         +
                "    `name_from`, "     +
                "    `charset`, "       +
                "    `secure`, "        +
                "    `success` "        +
                "FROM `a_my_users_emails` " +
                "WHERE " +
                "    `update` = 1;";

        ArrayList<User> users = new ArrayList<>();

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            rs   = stmt.executeQuery(query);

            if (rs.getRow() < 1) {
                return null;
            }

            while (rs.next()) {
                users.add(
                    new User(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getBoolean(5),
                        rs.getBoolean(6),
                        rs.getString(7),
                        rs.getInt(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getInt(13)
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;
        }

        return users;
    }

}
