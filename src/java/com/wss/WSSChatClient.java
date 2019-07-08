package com.wss;

import com.Main;
import com.classes.EmailAccount;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.threads.Mailing;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSSChatClient {
    private static WebSocket webSocket = null;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;

    public static boolean result = false;

    public WSSChatClient() {
        restart();
    }

    private void restart() {

        try {
            webSocketFactory = new WebSocketFactory();

            webSocketAdapter = new WebSocketAdapter(){
                @Override
                public void onTextMessage(WebSocket ws, String message) {
//                System.out.println("Get message ====================================== !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                try {
                    if (!message.contains("{")) {
                        System.err.println("|||" + message + "|||");
                        return;
                    }

                    JSONObject jsonArray = (JSONObject) getArrayFromJSON(message);

                    String command = String.valueOf(jsonArray.get("message"));
//                    System.err.println(command);

                    switch (command) {
                        case "restart":
                            System.out.println("===================================");
                            System.out.println(Main.mailing_tread.isAlive());
                            Main.is_restart = true;
                            System.out.println("===================================");
                            break;
                        case "stop":
                            System.out.println("========================STOP========================");
                            System.exit(0);
                            break;
                        case "close":
                            webSocket.sendClose();
                            break;
                        case "status":

                            webSocket.flush();

                            System.out.println("start accounts = " + Mailing.emailAccounts.size());

                            ConcurrentHashMap<Integer, EmailAccount> tmpEmailAccounts = new ConcurrentHashMap<>(Mailing.emailAccounts); // (ConcurrentHashMap<Integer, EmailAccount>) Mailing.emailAccounts; // TODO создать дубль

                            String json = getJsonFromMap(tmpEmailAccounts);

                            sendText("json", json);

                            System.gc();

                            break;
                        default:
                            System.out.println("Error command - " + command);
                    }
                } catch (Exception e) {
                    System.err.println("|||" + message + "|||");
                    e.printStackTrace();
                }
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
            if (webSocket != null) {
//                System.err.println("close WSS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                webSocket.sendClose();
                webSocket.disconnect();

//                webSocket.addListener(new WebSocketListener() {
//                    @Override
//                    public void onStateChanged(WebSocket websocket, WebSocketState newState) {
//                        System.err.println("onStateChanged");
//                    }
//
//                    @Override
//                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
//                        System.err.println("onStateChanged");
//                    }
//
//                    @Override
//                    public void onConnectError(WebSocket websocket, WebSocketException cause) {
//                        System.err.println("onConnectError");
//                    }
//
//                    @Override
//                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
//                        System.err.println("onDisconnected");
//                    }
//
//                    @Override
//                    public void onFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onFrame");
//                    }
//
//                    @Override
//                    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onContinuationFrame");
//                    }
//
//                    @Override
//                    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onTextFrame");
//                    }
//
//                    @Override
//                    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onBinaryFrame");
//                    }
//
//                    @Override
//                    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onCloseFrame");
//                    }
//
//                    @Override
//                    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onPingFrame");
//                    }
//
//                    @Override
//                    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onPongFrame");
//                    }
//
//                    @Override
//                    public void onTextMessage(WebSocket websocket, String text) {
//                        System.err.println("onTextMessage");
//                    }
//
//                    @Override
//                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
//                        System.err.println("onBinaryMessage");
//                    }
//
//                    @Override
//                    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onSendingFrame");
//                    }
//
//                    @Override
//                    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onFrameSent");
//                    }
//
//                    @Override
//                    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) {
//                        System.err.println("onFrameUnsent");
//                    }
//
//                    @Override
//                    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) {
//                        System.err.println("onThreadCreated");
//                    }
//
//                    @Override
//                    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) {
//                        System.err.println("onThreadStarted");
//                    }
//
//                    @Override
//                    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) {
//                        System.err.println("onThreadStopping");
//                    }
//
//                    @Override
//                    public void onError(WebSocket websocket, WebSocketException cause) {
//                        System.err.println("onError");
//                    }
//
//                    @Override
//                    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) {
//                        System.err.println("onFrameError");
//                    }
//
//                    @Override
//                    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) {
//                        System.err.println("onMessageError");
//                    }
//
//                    @Override
//                    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) {
//                        System.err.println("onMessageDecompressionError");
//                    }
//
//                    @Override
//                    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) {
//                        System.err.println("onTextMessageError");
//                    }
//
//                    @Override
//                    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) {
//                        System.err.println("onSendError");
//                    }
//
//                    @Override
//                    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
//                        System.err.println("onUnexpectedError");
//                    }
//
//                    @Override
//                    public void handleCallbackError(WebSocket websocket, Throwable cause) {
//                        System.err.println("handleCallbackError");
//                    }
//
//                    @Override
//                    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) {
//                        System.err.println("onSendingHandshake");
//                    }
//                });
            }

            restart();

            webSocket = webSocketFactory.createSocket("wss://my.tdfort.ru:10001/?ws_group=worker:javamail");
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

    public static void sendText(String subject, String text) {

        if (!subject.equals("json")) return;

//        if (webSocket != null && webSocket.isOpen()) {
        if (webSocket != null) {
            text = forJSON(text);
            webSocket.sendText("{\"subject\":\"" + subject + "\", \"message\":\"" + text + "\"}");
        } else {
            System.out.println("error send");
        }
    }

    public static String forJSON(String input) {
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
                case '\n': result.append("\\n");  break;
                case '\r': result.append("\\r");  break;
                case '\'': result.append("\\\'"); break;
                case '\"': result.append("\\\""); break;
                default: result.append(ch);       break;
            }
            ch = iterator.next();
        }
        return result.toString();
    }

    private static Object getArrayFromJSON(String jsonStr) {
        try {
            return new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            return false;
        }
    }

    private String getJsonFromMap(ConcurrentHashMap<Integer, EmailAccount> map) {
        StringBuffer tmpStr = new StringBuffer("{");

        for (Map.Entry<Integer, EmailAccount> e: map.entrySet()){
            tmpStr.append("\"").append(e.getKey()).append("\": ").append(e.getValue()).append(",");
        }

        return tmpStr.substring(0, tmpStr.length() - 1) + "}";
    }

}
