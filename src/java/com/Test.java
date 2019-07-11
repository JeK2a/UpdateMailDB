package com;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Test {
    public static void main(String[] args) {

        String tmp = "javax.mail.MessagingException: A1 BAD AUTHENTICATE Command syntax error. sc=sxarBhFnxmI1_110659_35o;<br>  nested exception is:<br>\tcom.sun.mail.iap.BadCommandException: A1 BAD AUTHENTICATE Command syntax error. sc=sxarBhFnxmI1_110659_35o<br>A1 BAD AUTHENTICATE Command syntax error. sc=sxarBhFnxmI1_110659_35o<br><br>com.sun.mail.imap.IMAPStore.protocolConnect(IMAPStore.java:668)<br>javax.mail.Service.connect(Service.java:295)<br>javax.mail.Service.connect(Service.java:176)<br>com.threads.MailingEmailAccountThread.connectToStore(MailingEmailAccountThread.java:275)<br>com.threads.MailingEmailAccountThread.run(MailingEmailAccountThread.java:46)<br>java.base/java.lang.Thread.run(Thread.java:834)";

        System.out.println(forException(tmp));

    }

    public static String forException(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int len = input.length();
        // сделаем небольшой запас, чтобы не выделять память потом
        final StringBuilder result = new StringBuilder(len + len / 4);
        final StringCharacterIterator iterator = new StringCharacterIterator(input);
        char ch = iterator.current();

        while (ch != CharacterIterator.DONE) {
            switch (ch) {
                case '\n': result.append("<br>");  break;
                case '\r': result.append("<br>");  break;
                case '\t': result.append(" ");
                    System.out.println("Tab");
                break;
                default: result.append(ch);        break;
            }
            ch = iterator.next();
        }
        return result.toString();
    }
}
