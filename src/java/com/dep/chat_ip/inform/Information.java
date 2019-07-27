package com.dep.chat_ip.inform;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Information {

    public static String getHostName() {

        String hostName = "";

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println(e);
        }

        return hostName;
    }

    public static String getHostAddress() {

        String hostAddress = "";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println(e);
        }

        return hostAddress;
    }

    public static String getWhoIm() {
        return  (getHostName() + " - " + getHostAddress());
    }
}
