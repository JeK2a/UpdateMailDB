package com.service;

import com.classes.EmailAccount;
import com.classes.MyFolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyPrint {

    public static void printArrayList(ArrayList<EmailAccount> arrayList) {
        for (EmailAccount emailAccount : arrayList) {
            printEmailAccount(emailAccount);
        }
    }

    public static void printHashMap(HashMap<String, MyFolder> hashMap) {
        for (Map.Entry<String, MyFolder> entry : hashMap.entrySet()) {
            System.out.println("            " + entry.getKey() + " : " + entry.getValue());
        }
    }

    public static void printEmailAccount(EmailAccount emailAccount) {
        System.out.println(emailAccount);
        System.out.println(emailAccount.getUser().getEmail() + " = " + emailAccount.getUser());
        printHashMap(emailAccount.getMyFoldersMap());
    }


//    public static String getStrinfArrayList(ArrayList<MyFolder> arrayList) {
//        StringBuilder str = new StringBuilder("\n");
//
//        for (MyFolder myFolder : arrayList) {
//            str.append(myFolder).append("\n");
//        }
//
//        return str.toString();
//    }

    public static String getStrinfArrayList(HashMap<Integer, EmailAccount> hashMap) {
        StringBuilder str = new StringBuilder("\nEmailAccounts\n");
        for (Map.Entry<Integer, EmailAccount> entry : hashMap.entrySet()) {
            str.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }

        return str.toString();
    }

    public static String getStringHashMap(HashMap<String, MyFolder> hashMap) {
        StringBuilder str = new StringBuilder("\n");

        for (Map.Entry<String, MyFolder> entry : hashMap.entrySet()) {
            str.append("            " + entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }

        return str.toString();
    }

}
