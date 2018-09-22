package ws_client;

import java.util.*;
import javax.websocket.Session;
public class PushTimeService implements Runnable {

    private static PushTimeService instance;
    private static Map<String, Session> sMap = new HashMap<>();
    private PushTimeService() {}

    public static void add(Session s) {
        sMap.put(s.getId(), s);
    }

    public static void initialize() {
        if (instance == null) {
            instance = new PushTimeService();
            new Thread(instance).start();
        }
    }
    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(10 * 1000);
                for (String key : sMap.keySet()) {

                    Session s = sMap.get(key);

                    if (s.isOpen()) {
                        Date d = new Date(System.currentTimeMillis());
                        s.getBasicRemote().sendText(d.toString());
                    } else {
                        sMap.remove(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}