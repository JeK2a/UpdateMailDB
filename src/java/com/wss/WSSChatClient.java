package com.wss;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import javax.net.ssl.SSLContext;

public class WSSChatClient {

    private static WebSocket webSocket;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;

    public static boolean result = false;

//    private final String url = "wss://my.tdfort.ru:8897";
    private final String url = "wss://my.tdfort.ru:10001/?ws_group=worker:javamail";

    public WSSChatClient() {
        try {
            webSocketFactory = new WebSocketFactory();
            webSocketAdapter = new WebSocketAdapter(){
                @Override
                public void onTextMessage(WebSocket ws, String message) {
                    System.out.println(message);
                            ws.disconnect();
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
        if (result) {
            webSocket.sendText("{\"subject\":\"\", \"message\":\"" + text + "\"}");
        }
    }

    public void sendText(StringBuilder stringBuilder) {
        sendText(stringBuilder.toString());
    }

    public void sendText(Object o) {
        sendText(String.valueOf(o));
    }

}
