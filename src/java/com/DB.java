package com;

import com.classes.Email;
import com.classes.MyMessage;
import com.classes.User;
import com.service.Settings;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class DB implements AutoCloseable {

    private static final String[] params = {
                                               "useSSL=false",
                                               "serverTimezone=UTC"

                                           };
    private static Connection        con;
    private static Statement         stmt;
    private static PreparedStatement prep_stmt;
    private static ResultSet         rs;
    private ResultSetMetaData rsmd;
    private static boolean is_line = false;

    public DB() {
        new Settings();

        String USER     = Settings.getUser();
        String PASSWORD = Settings.getPassword();
        String HOST     = Settings.getHost();
        String PORT     = Settings.getPort();
        String SCHEMA   = Settings.getSchema();
        String URL      = "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA;

//        params[0] = "useSSL="            + Settings.getUsessl();
//        params[1] = "useUnicode="        + Settings.getUseunicode();
//        params[2] = "characterEncoding=" + Settings.getCharacterencoding();

        try {
//            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL + "?" + arrayToString(params, "&"), USER, PASSWORD); // JDBC подключение к MySQL
//            con = DriverManager.getConnection(URL, USER, PASSWORD); // JDBC подключение к MySQL
//            System.err.println(URL + "?" + arrayToString(params, "&"));

            if (con == null) {                              // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                System.exit(0);                      // И выйти из программы
            }

            stmt = con.createStatement(); // getting Statement object to execute query
        } catch(SQLException | ClassNotFoundException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public boolean addEmail(Email email) {

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

            "    `time`, \n"        + //26
            "    `email_account` \n"  + //27
            ") \n"                 +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n" +
            "    ON DUPLICATE KEY UPDATE \n" +
            "        `folder`        = VALUES(`folder`), \n" +
            "        `email_account` = VALUES(`email_account`), \n" +
            "        `uid`           = VALUES(`uid`);";

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            prep_stmt = con.prepareStatement(query);

            prep_stmt.setString(1, email.getDirection());
            prep_stmt.setInt(2, email.getUser_id());
            prep_stmt.setInt(3, email.getClient_id());
            prep_stmt.setLong(4, email.getUid());
            prep_stmt.setString(5, email.getMessage_id());
            prep_stmt.setString(6, email.getFrom());
            prep_stmt.setString(7, email.getTo());
            prep_stmt.setString(8, email.getIn_replay_to());
            prep_stmt.setString(9, email.getReferences());
            prep_stmt.setTimestamp(10, email.getDate());
            prep_stmt.setLong(11, email.getSize());
            prep_stmt.setString(12, email.getSubject());
            prep_stmt.setString(13, email.getFolder());

            prep_stmt.setInt(14, email.getFlagged());
            prep_stmt.setInt(15, email.getAnswred());
            prep_stmt.setInt(16, email.getDeleted());
            prep_stmt.setInt(17, email.getSeen());
            prep_stmt.setInt(18, email.getDraft());
            prep_stmt.setInt(19, email.getForwarded());
            prep_stmt.setInt(20, email.getLabel1());
            prep_stmt.setInt(21, email.getLabel2());
            prep_stmt.setInt(22, email.getLabel3());
            prep_stmt.setInt(23, email.getLabel4());
            prep_stmt.setInt(24, email.getLabel5());
            prep_stmt.setInt(25, email.getHas_attachment());

            prep_stmt.setTimestamp(26, email.getUpdate());
            prep_stmt.setString(27, email.getEmail_account());

            prep_stmt.executeUpdate();
        } catch (Exception e) {
//            System.err.println(query);
//            System.err.println(email);
//            System.err.println("email.getFolder() = ");
//            System.err.println(email.getFolder());
//            e.printStackTrace();

            return addEmail(email);
        } finally {
            is_line = false;
        }

        return true;
    }


    public ArrayList<User> getUsers() {

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
            "FROM `" + Settings.getTable_users() + "` " +
            "WHERE " +
                "`is_monitoring` = 1 AND " +
                "`success` = 1";

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

        String query = "" +
                "SELECT COUNT(`uid`) "            +
                "FROM `a_api_emails` " +
                "WHERE " +
                "    `email_account` = '" + email_account + "' AND " +
                "    `folder` = '" + folder_name + "' ";

        long count_messages = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                count_messages = rs.getLong(1);
            }

        } catch (Exception e) {
//            System.err.println("================================================");
//            System.err.println(query);
//            System.err.println("================================================");
//            e.printStackTrace();

            return getCountMessages(email_account, folder_name);
        } finally {
            is_line = false;
        }

        return count_messages;
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

        StringBuilder str = new StringBuilder();

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
                "    `flagged`   = 0, " +
                "    `answered`  = 0, " +
                "    `deleted`   = 0, " +
                "    `seen`      = 1, " +
                "    `draft`     = 0, " +
                "    `forwarded` = 0, " +
                "    `label_1`   = 0, " +
                "    `label_2`   = 0, " +
                "    `label_3`   = 0, " +
                "    `label_4`   = 0, " +
                "    `label_5`   = 0, " +
                "    `has_attachment` = 0 " +
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
                "   `folder` = '"+flag_name+"' AND " +
                "   `uid` IN ("+uids+");";

        int result = 0;

        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            result = stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(query);
            e.printStackTrace();
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

    public int checkDelete(int user_id, String folder_name, long uid_start, long uid_end, String uids) {
        String query = "" +
                "UPDATE `a_api_emails` " +
                "SET `deleted` = 1, " +
                "    `time` = '" + new java.sql.Timestamp(new Date().getTime()) + "' " +
                "WHERE  " +
                "   `uid` >= '"+uid_start+"' AND '"+uid_start+"' <= `uid`  AND " +
                "   `folder` = '"+folder_name+"' AND `user_id` = '"+user_id+"' AND " +
                "   `uid` NOT IN ("+uids+");";

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
            "    `last_add_uid`,   " +
            "    `update_time`     " +
            ") VALUES ( " +
            " "  + email.getUser_id() + ", "  +
            " '" + email_address      + "', " +
            " '" + email.getFolder()  + "', " +
            " "  + email.getUid()     + ", "  +
            " '" + new Timestamp(new Date().getTime()) + "' " +
            ") ON DUPLICATE KEY UPDATE" +
            " `last_add_uid` = VALUES(`last_add_uid`)," +
            " `update_time`  = VALUES(`update_time`);";
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

    public long getLastAddUID(int user_id, String account_email, String folder_name) {
        long last_add_uid = 0;

        String query = "" +
                "SELECT `last_add_uid` " +
                "FROM `a_api_email_folders` " +
                "WHERE " +
                "    `user_id`       = '"+ user_id +"' AND " +
                "    `account_email` = '"+ account_email +"' AND " +
                "    `folder_name`   = '"+ folder_name+"' ;";
        try {
            if (is_line) {
                Thread.sleep(100);
            }
            is_line = true;

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs == null) {
                return 0;
            }
            if (rs.next()) {
                last_add_uid = rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  0;
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
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                return new MyMessage(
                    rs.getString("direction"),
                    rs.getInt("user_id"),
                    rs.getInt("client_id"),
                    rs.getLong("uid"),
                    rs.getString("message_id"),
                    rs.getString("from"),
                    rs.getString("to"),
                    rs.getString("in_reply_to"),
                    rs.getString("references"),
                    rs.getTimestamp("message_date"),
                    rs.getLong("size"),
                    rs.getString("subject"),
                    rs.getString("folder"),
                    rs.getInt("flagged"),
                    rs.getInt("answered"),
                    rs.getInt("deleted"),
                    rs.getInt("seen"),
                    rs.getInt("draft"),
                    rs.getInt("forwarded"),
                    rs.getInt("label_1"),
                    rs.getInt("label_2"),
                    rs.getInt("label_3"),
                    rs.getInt("label_4"),
                    rs.getInt("label_5"),
                    rs.getInt("has_attachment"),
                    rs.getTimestamp("time"),
                    rs.getString("email_account")
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
