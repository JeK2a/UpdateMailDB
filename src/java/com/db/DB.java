package com.db;

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
    private static boolean is_line = false;
    private static int count_errors = 0;
    public static boolean result = false;

    public static int count_queries = 0;

    private String USER;
    private String PASSWORD;
    private String HOST;
    private String PORT;
    private String SCHEMA;
    private String URL;

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

//            Statement stmt = con.createStatement(); // getting Statement object to execute query
        } catch(Exception e) {
            result = false;
            e.printStackTrace();

            return false;
        } finally {
            DB.result = result;
            return result;
        }
    }


    public static void incCount_queries() {
        DB.count_queries++;
    }

    public static int getCount_queries() {
        return count_queries;
    }

    public static void setCount_queries(int count_queries) {
        DB.count_queries = count_queries;
    }

    public boolean addEmail(Email email) {

        if (email.getFolder() == null) {
            return false;
        }

        String query = "" +
            "INSERT INTO `a_api_emails`(\n" +
            "    `direction`, \n"      + // 1
            "    `user_id`, \n"        + // 2
            "    `client_id`, \n"      + // 3
            "    `uid`, \n"            + // 4
            "    `message_id`, \n"     + // 5
            "    `from`, \n"           + // 6
            "    `to`, \n"             + // 7
            "    `in_reply_to`, \n"    + // 8
            "    `references`, \n"     + // 9
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
            "    `email_account`, \n"  + //27
            "    `tdf_id` \n"          + //28
            ") \n"                     +
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
            incCount_queries();
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
            if (count_errors < 10 && connectToDB()) {
                addEmail(email);
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
//                "   `user_id` = 264 AND " +
//            "   `users`.`email` = \"me@tdfort.ru\" AND " +
            "   `users`.`is_monitoring` = 1 " +
            "ORDER  BY `users`.`email`;"
//                +
//                "`success` = 1"
                ; // TODO success

        ArrayList<User> users = new ArrayList<>();

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            Statement stmt = con.createStatement();
            incCount_queries();
            ResultSet rs_tmp = stmt.executeQuery(query);

            while (rs_tmp.next()) {
                users.add(
                    new User(
                        rs_tmp.getInt(1),
                        rs_tmp.getInt(2),
                        rs_tmp.getString(3),
                        rs_tmp.getString(4),
                        rs_tmp.getBoolean(5),
                        rs_tmp.getBoolean(6),
                        rs_tmp.getString(7),
                        rs_tmp.getInt(8),
                        rs_tmp.getString(9),
                        rs_tmp.getString(10),
                        rs_tmp.getString(11),
                        rs_tmp.getString(12),
                        rs_tmp.getInt(13)
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

    public long getCountMessages(String email_account, String folder_name) {
        String query = "" +
                "SELECT COUNT(`uid`) "            +
                "FROM `a_api_emails` " +
                "WHERE " +
                "    `email_account` = \"" + email_account + "\" AND " +
                "    `folder`        = \"" + folder_name   + "\" AND " +
                "    `removed`       = 0;" ;

        long count_messages = 0;

        try {

            if (con.isClosed()) {
                connectToDB();
            }

            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            incCount_queries();
            Statement stmt = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            if (rs_tmp.next()) {
                count_messages = rs_tmp.getLong(1);
            }

            rs_tmp.close();
            stmt.close();
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
                "SET `success` = '" + success + "' " +
                "WHERE  `email` = '" + email_address + "';";

        int result = 0;

        try {
            if (con.isClosed()) {
                connectToDB();
            }

            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            incCount_queries();
            Statement stmt = con.createStatement();
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

    public boolean updateQuery(String query) {
        Statement stmt = null;

        try {
            if (con.isClosed()) {
                connectToDB();
            }

            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            incCount_queries();

            stmt = con.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
            if (connectToDB()) {
                updateQuery(query);
            }
        } catch (Exception e) {
            System.err.println("query======================================================================");
            System.err.println(query);
            System.err.println("query======================================================================");
            e.printStackTrace();
        } finally {
            is_line = false;

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    // TODO
                }
            }
        }

        return true;
    }

    public boolean changeFolderName(Email email, String new_folder_name) { // TODO изменить у сообщений имя папки (проверить)

        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET " +
                "    `folder` = '" + new_folder_name   + "', " +
                "    `time`   = '" + email.getUpdate() + "'  " +
                "WHERE" +
                "    `folder`     = '" + email.getFolder()     + "' AND " +
                "    `message_id` = '" + email.getMessage_id() + "';";

        return updateQuery(query);
    }

    @Override
    public void close() {
        try {
            assert con  != null; if (con  != null) con.close();
//            assert stmt != null; if (stmt != null) stmt.close();
//            assert rs   != null; if (rs   != null) rs.close();
        } catch(SQLException ignored) { }
    }

    public String arrayToString(String[] arr, String symbol) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            str.append(arr[i]);

            if (i != (arr.length - 1)) {
                str.append(symbol);
            }
        }

        return str.toString();
    }

    public boolean setDeleteFlag(String email_address, String folder_name, long uid) { // TODO изменение флага сообщенией на удаленное (проверить)

        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET " +
                "    `deleted` = 1,    " +
                "    `removed` = 1,    " +
                "    `time`    = NOW() " +
                "WHERE " +
                "    `email_account` = '" + email_address + "' AND " +
                "    `folder`        = '" + folder_name   + "' AND " +
                "    `uid`           = '" + uid           + "';";

        return updateQuery(query);
    }

    public boolean setFlags(String email, String folder_name) {
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

        if (email.equals("")) {
            query += String.format(" AND `email_account` = '%d' ", email);
        }
        if (folder_name != null) {
            query += String.format(" AND `folder` = '%s' ", folder_name);
        }

        return updateQuery(query);
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

            Statement stmt = con.createStatement();
            result = stmt.executeUpdate(query);
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return setFlags(user_id, folder_name, flag_name, flag_value, uids);
        } finally {
            is_line = false;
        }

        return result;
    }

    public boolean setRemoved(String email, String folder_name, long uid_start, long uid_end, ArrayList<Long> uids) {
        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET " +
                "    `removed` = 1, " +
                "    `time` = NOW() " +
                "WHERE  " +
                "    `folder` = '" + folder_name + "' AND `email_account` = '" + email + "' ";

        if (uid_start > -1) {
            query += " AND `uid` >= " + uid_start + " ";
        }

        if (uid_end > -1) {
            query += " AND `uid` <= " + uid_end + " ";
        }

        if (uids != null && uids.size() > 0) {
            StringBuilder str_uids = new StringBuilder(String.valueOf(uids.get(0)));

            for (int n = 1; n < uids.size(); n++) {
                str_uids.append(",").append(String.valueOf(uids.get(n)));
            }

            query += " AND `uid` NOT IN (" + str_uids + ") ";
        }

        return updateQuery(query);
    }

    public long[] getMissingUIDs(String email_address, String folder_name, long uid_start, long uid_end, ArrayList<Long> uids) { // TODO на акк

        long[] missing_uids = new long[0];

        String query = "" +
                "SELECT `uid` " +
                "FROM `a_api_emails` " +
                "WHERE  " +
                "   `folder` = '" + folder_name  + "' " +
                "   AND `email_account` = '" + email_address + "' " +
                "   AND `uid` >= " + uid_start   + " " +
                "   AND `uid` <= " + uid_end     + " ";

        if (uids != null && uids.size() > 0) {
            StringBuilder str_uids = new StringBuilder(String.valueOf(uids.get(0)));

            for (int n = 1; n < uids.size(); n++) {
                str_uids.append(",").append(String.valueOf(uids.get(n)));
            }

            query += " AND `uid` IN (" + str_uids + ") ";
        }

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            Statement stmt = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            assert uids != null;
            ArrayList<Long> uids_tmp = (ArrayList<Long>) uids.clone();

            while (rs_tmp.next()) {
                uids_tmp.remove(rs_tmp.getLong(1));
            }

            missing_uids = new long[uids_tmp.size()];

            for (int i = 0; i < uids_tmp.size(); i++) {
                missing_uids[i] = uids_tmp.get(i);
            }

            rs_tmp.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is_line = false;

        }

        return missing_uids;
    }

    public boolean deleteMessage(int user_id, String folder_name, long uid) {
        String query = "" +
                "DELETE `a_api_emails` " +
                "SET `deleted` = 1, " +
                "    `time` = '" + new java.sql.Timestamp(new Date().getTime()) + "' " +
                "WHERE  `user_id` = '"+user_id+"' AND " +
                "    `folder` = '"+folder_name+"' AND " +
                "    `uid` = '"+uid+"'";

        return updateQuery(query);
    }

//    public boolean updateAccountError(int id,  String error_text) {
//        return updateQuery(id, "error", error_text);
//    }

    public boolean updateQuery(int id, String key, String value) {
        String query = "" +
                "UPDATE `a_my_users_emails` " +
                "SET `" + key + "` = '" + value + "' " +
                "WHERE `id` = " + id + ";";

        return updateQuery(query);
    }

    public long getLastAddUID(String account_email, String folder_name) {
        long last_add_uid = 0;

        String query = "" +
                "SELECT `uid` " +
                "FROM `a_api_emails` " +
                "WHERE" +
                "   `email_account` = \"" + account_email +"\" AND " +
                "   `folder`        = \"" + folder_name + "\"  AND " +
                "   `removed`       = 0 " +
                "ORDER BY `uid` DESC " +
                "LIMIT 1";

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            Statement stmt = con.createStatement();
            ResultSet rs_tmp = stmt.executeQuery(query);

            if (rs_tmp.next()) {
                last_add_uid = rs_tmp.getLong(1);
            }

            rs_tmp.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return getLastAddUID(account_email, folder_name);
        } finally {
            is_line = false;
        }

        return last_add_uid;
    }

    public boolean deleteMessages(String email_address, String folder_name) {
        String query = "" +
                "DELETE FROM `a_api_emails` " +
                "WHERE " +
                "    `email_account` = '" + email_address + "' AND " +
                "    `folder` = '" + folder_name + "';";

        return updateQuery(query);
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
                Thread.sleep(100);
            }
            is_line = true;

            Statement stmt = con.createStatement();
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

            rs_tmp.close();
            stmt.close();
        } catch (Exception e) {
            return getMyMessage(email_address, folder_name, uid);
        } finally {
            is_line = false;
        }

        return null;
    }

}