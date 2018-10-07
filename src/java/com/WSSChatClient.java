package com;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class WSSChatClient {

    private static WebSocket webSocket;
    private final String url = "wss://my.tdfort.ru:8897";

    public WSSChatClient() {
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        WebSocketAdapter webSocketAdapter = new WebSocketAdapter(){
            @Override
            public void onTextMessage(WebSocket ws, String message) {
//                System.out.println(message);
//                        ws.disconnect();
            }
        };

        try {
            webSocket = webSocketFactory.createSocket(url);
            webSocket.addListener(webSocketAdapter);
            webSocket.connect();
            webSocket.sendText("{\"act\":\"start\",\"user_id\":\"1000\",\"user_name\":\"Mailler\",\"msg\":\"Подключение установлено обоюдно, отлично!\"}");
        } catch (IOException | WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void sendText(String text) {
        webSocket.sendText("{\"act\":\"msg\", \"msg\":\"" + text + "\", \"room_id\":\"1000\"}");

    }

    public void sendText(StringBuilder stringBuilder) {
        sendText(stringBuilder.toString());
    }

    public void sendText(Object o) {
        sendText(String.valueOf(o));
    }



}
