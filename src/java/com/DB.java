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
                                               "useUnicode=true",
                                               "characterEncoding=utf-8"
                                           };
    private static Connection        con;
    private static Statement         stmt;
    private static PreparedStatement prep_stmt;
    private static ResultSet         rs;
    private static ResultSetMetaData rsmd;

    public DB() {
        new Settings();

        String USER     = Settings.getUser();
        String PASSWORD = Settings.getPassword();
        String HOST     = Settings.getHost();
        String PORT     = Settings.getPort();
        String SCHEMA   = Settings.getSchema();
        String URL      = "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA;

        params[0] = "useSSL="            + Settings.getUsessl();
        params[1] = "useUnicode="        + Settings.getUseunicode();
        params[2] = "characterEncoding=" + Settings.getCharacterencoding();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL + "?" + arrayToString(params, "&"), USER, PASSWORD); // JDBC подключение к MySQL
//            System.err.println(URL + "?" + arrayToString(params, "&"));

            if (con == null) {                              // Если подключение к БД не установлено
                System.err.println("Нет соединения с БД!"); // Вывести ошибку
                System.exit(0);                      // И выйти из программы
            }

            stmt = con.createStatement(); // getting Statement object to execute query
        } catch(SQLException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    public boolean addEmail(Email email) {

        String query = "" +
//            "INSERT INTO `" + Settings.getTable_emails() + "`(" +
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
            "    `message_date`, \n"        + //11
            "    `size`, \n"        + //12
            "    `subject`, \n"     + //13
            "    `folder`, \n"      + //14

            "    `flagged`, \n"     + //16
            "    `answered`, \n"    + //17
            "    `deleted`, \n"     + //18
            "    `seen`, \n"        + //19
            "    `draft`, \n"       + //20

            "    `forwarded`, \n"   + //22
            "    `label_1`, \n"      + //23
            "    `label_2`, \n"      + //24
            "    `label_3`, \n"      + //25
            "    `label_4`, \n"      + //26
            "    `label_5`, \n"      + //27
            "    `has_attachment`, \n" + //28

            "    `time`, \n"        + //29
            "    `email_account` \n"  + //30
            ") \n"                 +
            "VALUES ( \n" +
            "    '"+email.getDirection() + "',\n" +
            "    "+email.getUser_id()+",\n" +
            "    "+email.getClient_id()+",\n" +
            "    "+email.getUid()+",\n" +
            "    '"+email.getMessage_id().replace("'", "\\'") + "',\n" +
            "    '"+email.getFrom().replace("'", "\\'")+"',\n" +
            "    '"+email.getTo().replace("'", "\\'")+"', \n" +
            "    '"+email.getIn_replay_to().replace("'", "\'")+"',\n" +
            "    '"+email.getReferences().replace("'", "\\'")+"',\n" +
            "    '"+email.getDate()          + "',\n" +
            "    "+email.getSize()           + ",\n" +
            "    ?,\n" +
            "    '"+email.getFolder()       + "',\n" +

            "    "+email.getFlagged()       + ",\n" +
            "    "+email.getAnswred()       + ",\n" +
            "    "+email.getDeleted()       + ",\n" +
            "    "+email.getSeen()          + ",\n" +
            "    "+email.getDraft()         + ",\n" +
            "    "+email.getForwarded()     + ",\n" +
            "    "+email.getLabel1()        + ",\n" +
            "    "+email.getLabel2()        + ",\n" +
            "    "+email.getLabel3()        + ",\n" +
            "    "+email.getLabel4()        + ",\n" +
            "    "+email.getLabel5()        + ",\n" +
            "    "+email.getHas_attachment()+ ",\n" +

            "    '"+email.getUpdate()        + "',\n" +
            "    '"+email.getEmail_account() + "' \n" +
            ") \n" +
            "    ON DUPLICATE KEY UPDATE \n" +
            "        `folder`        = VALUES(`folder`), \n" +
            "        `email_account` = VALUES(`email_account`), \n" +
            "        `uid`           = VALUES(`uid`);";

        try {
            prep_stmt = con.prepareStatement(query);
            prep_stmt.setString(1, email.getSubject());
            prep_stmt.executeUpdate();
        } catch (SQLException e) {
//            System.err.println("==================================" + email.getSubject() + "==================================");
//            System.err.println(query);
//            e.printStackTrace();
            return false;
        }

        return true;
    }


    public ArrayList<User> getUsers() {

        String query = "" +
            "SELECT " +
                " `id`, "            +
                " `user_id`, "       +
                " `email`, "         +
                " `password`, "      +
                " `is_monitoring`, " +
                " `is_default`, "    +
                " `host`, "          +
                " `port`, "          +
                " `login`, "         +
                " `name_from`, "     +
                " `charset`, "       +
                " `secure`, "        +
                " `success` "        +
            "FROM `" + Settings.getTable_users() + "` " +
            "WHERE " +
                "`is_monitoring` = 1 AND " +
                "`success` = 1";

        ArrayList<User> users = new ArrayList<>();

        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public long getLastUID(String account_email, String folder_name) {

//        "FROM `" + Settings.getTable_emails() + "` " +

        String query = "" +
                "SELECT MAX(`uid`) "            +
                "FROM `a_api_email_folders` " +
                "WHERE " +
                "    `account_email` = '"+account_email+"' AND " +
                "    `folder` = '"+folder_name+"' ";

        long last_uid = 0;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs == null) { return 0; }
            if (rs.next()) { last_uid = rs.getLong(1); }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        return last_uid;
    }

    public int getCountMessages(String email_account, String folder_name) {

//        "FROM `" + Settings.getTable_emails() + "` " +

        String query = "" +
                "SELECT COUNT(`uid`) "            +
                "FROM `a_api_emails` " +
                "WHERE " +
                "    `email_account` = '" + email_account + "' AND " +
                "    `folder` = '" + folder_name + "' ";

        int count_messages = 0;
        String count_messages_str = "";

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs == null) {
                return 0;
            }

            if (rs.next()) {
                count_messages_str = rs.getString(1);
            }

        } catch (SQLException e) {
//            System.out.println(query);
//            System.out.println(count_messages_str);
//            e.printStackTrace();
            return 0;
        }

        count_messages = Integer.parseInt(count_messages_str);

        return count_messages;
    }

    public int changeFolderName(Email email, int user_id, String new_folder_name) { // TODO изменить у сообщений имя папки (проверить)

//        "UPDATE `" + Settings.getTable_emails() + "` " +

        String query = "" +
                "UPDATE `a_my_emails` " +
                "SET " +
                    "`folder` = '" + new_folder_name   + "', " +
                    "`time`  = '" + email.getUpdate() + "'  " +
                "WHERE" +
                    "`folder`     = '" + email.getFolder() + "' AND " +
                    "`message_id` = '" + email.getMessage_id() + "';";

        int result = 0;

        try {
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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

//        "UPDATE `" + Settings.getTable_emails() + "` " +

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
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(query);
            e.printStackTrace();
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
        } catch (SQLException e) {
            e.printStackTrace();
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
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            if (rs == null) {
                return 0;
            }
            if (rs.next()) {
                last_add_uid = rs.getLong(1);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            return  0;
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
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                last_event_uid = rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
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

        int count_col = 0;
        int count_row = 0;

        ResultSetMetaData meta = null;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

//            meta = rs.getMetaData();

            if (rs == null) {
                return null;
            }

//            count_col = meta.getColumnCount();
//            count_row = rs.getRow();
//
//            System.err.println("---------------------------------");
//            System.err.println("count col = " + count_col);
//            System.err.println("count row = " + count_row);
//            System.err.println("---------------------------------");

            if (rs.next()) {
                count_row = rs.getRow();

                if (count_row < 1) {
                    return null;
                }

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
//            System.err.println("---------------------------------");
//            System.err.println(query);
//            System.err.println("---------------------------------");
//            System.err.println("count col = " + count_col);
//            System.err.println("count row = " + count_row);
//            System.err.println("---------------------------------");

//            for (int i = 1; i <= count_col; i++) {
//                try {
//                    System.err.println(meta.getColumnName(i));
//                } catch (SQLException e1) {
//                    e1.printStackTrace();
//                }
//            }

//            e.printStackTrace();
            return null;
        }

        return null;
    }

    public ArrayList<User> getUsersUpdate() {

//        "FROM `" + Settings.getTable_users() + "` " +

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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

}
