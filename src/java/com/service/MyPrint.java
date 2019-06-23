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

//    public static String getStringFromEmailAccounts(ArrayList<MyFolder> arrayList) {
//        StringBuffer str = new StringBuffer("\n");
//
//        for (MyFolder myFolder : arrayList) {
//            str.append(myFolder).append("\n");
//        }
//
//        return str.toString();
//    }

    public static String getStringFromEmailAccounts(HashMap<Integer, EmailAccount> hashMap) {
        StringBuffer str = new StringBuffer("\nEmailAccounts\n");

        if (hashMap == null) {
            System.err.println("getStringFromEmailAccounts IS NULL (hashMap)!!!!!!!!!!!!!");
        } else {

            HashMap<Integer, EmailAccount> hashMap_clone = (HashMap<Integer, EmailAccount>) hashMap.clone();

            for (Map.Entry<Integer, EmailAccount> entry : hashMap_clone.entrySet()) {
                str.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }
        }

        return str.toString();
    }

    public static String getStringFromMyFolders(HashMap<String, MyFolder> hashMap) {
        StringBuffer str = new StringBuffer("\nMyFolders\n");

        if (hashMap == null) {
            System.err.println("getStringFromMyFolders IS NULL (hashMap)!!!!!!!!!!!!!");
        } else {

            HashMap<String, MyFolder> hashMap_clone = (HashMap<String, MyFolder>) hashMap.clone();

            for (Map.Entry<String, MyFolder> entry : hashMap_clone.entrySet()) {
                str.append("            " + entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
            }
        }

        return str.toString();
    }

}
