package com.dep.chat_ip.test_t;

import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {

//        String[] arr = {"aaa", "bbb"};
//
//        for (String str : arr) {
//            arr[3] = "ccc";
//            System.out.println(str);
//        }

//        Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
////        Set<String> set = new HashSet<>();
//
//        set.add("aaa");
//        set.add("bbb");
//
//        for (String s : set) {
//            set.add("ccc");
//            System.out.println(s);
//        }


        ConcurrentHashMap<Integer, String> hashMap = new ConcurrentHashMap<>();

        hashMap.put(0, "aaa");
        hashMap.put(1, "bbb");

//        Iterator iter = ;

//        HashMap<Integer, String> hashMap_clone = (HashMap<Integer, String>) hashMap.clone();

//        Iterator it = hashMap.entrySet().iterator();
//        Iterator it = hashMap_clone.entrySet().iterator();

//        for (Map.Entry<Integer, String> entry : hashMap.clone().entrySet()) {
//            Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) it.next();
//            hashMap.put(2, "ccc");
//            System.out.println(entry.getKey() + " = " + entry.getValue());


//        Iterator it = hashMap.entrySet().iterator();
//        while (it.hasNext())
//        {
//            Map.Entry item = it.next();
//            it.remove(item);
//        }


    }
}


class Testtt implements Runnable {

    @Override
    public void run() {

        System.out.println("Thander");
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("E");
        } finally {
            System.out.println('F');
        }
    }
}
