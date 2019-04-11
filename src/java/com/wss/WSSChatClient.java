package com.wss;

import com.threads.Mailing;
import com.Main;
import com.classes.EmailAccount;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.service.MyPrint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLContext;
import java.util.HashMap;

public class WSSChatClient {

    public static HashMap<Integer, EmailAccount> emailAccounts = Mailing.emailAccounts;

    private static WebSocket webSocket;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;

    public static boolean result = false;

    private final String url = "wss://my.tdfort.ru:10001/?ws_group=worker:javamail";

    public WSSChatClient() {
        try {
            webSocketFactory = new WebSocketFactory();
            webSocketAdapter = new WebSocketAdapter(){
                @Override
                public void onTextMessage(WebSocket ws, String message) {
                    try {
                        JSONParser jsonParser = new JSONParser();
                        Object object = jsonParser.parse(message);
                        JSONObject jsonArray = (JSONObject) object;

                        String command = String.valueOf(jsonArray.get("message"));
                        System.out.println(command);

                        switch (command) {
                            case "restart":
                                System.out.println("===================================");
                                System.out.println(Main.mailing_tread.isAlive());
                                Main.is_restart = true;
                                System.out.println(Main.mailing_tread.isAlive());
                                System.out.println("===================================");
                                break;
                            case "stop":
                                break;
                            case "test":
                                System.out.println(MyPrint.getStrinfArrayList(emailAccounts));
                                sendText(MyPrint.getStrinfArrayList(emailAccounts));
                                break;
                            default:
                                System.out.println("Error command - " + command);
                        }
                    } catch (ParseException e) {
                        System.err.println("|||" + message + "|||");
                        e.printStackTrace();
                    }

//                    System.out.println(message);
//                            ws.disconnect();
                }
            };

            // Create a custom SSL context.
            SSLContext context = NaiveSSLContext.getInstance("TLS");

            // Set the custom SSL context.
            webSocketFactory.setSSLContext(context);
            webSocketFactory.setVerifyHostname(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connectToWSS() {
        boolean result = true;
        try {
            webSocket = webSocketFactory.createSocket(url);
            webSocket.addListener(webSocketAdapter);
            webSocket.connect();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            WSSChatClient.result = result;
            return result;
        }
    }

    public void sendText(String text) {
        sendText("", text);
    }

    public void sendText(String subject, String text) {
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendText("{\"subject\":\"" + subject + "\", \"message\":\"" + text + "\"}");
        }
    }

    public void sendText(StringBuilder stringBuilder) {
        sendText(stringBuilder.toString());
    }

    public void sendText(Object o) {
        sendText(String.valueOf(o));
    }

}
