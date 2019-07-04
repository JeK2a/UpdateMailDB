package com.wss;

import com.Main;
import com.classes.EmailAccount;
import com.neovisionaries.ws.client.*;
import com.threads.Mailing;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSSChatClient {
    private static WebSocket webSocket = null;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;

    public static boolean result = false;

    private final String url = "wss://my.tdfort.ru:10001/?ws_group=worker:javamail";

    public WSSChatClient() {
        restart();
    }

    private void restart() {

        try {
            webSocketFactory = new WebSocketFactory();

            webSocketAdapter = new WebSocketAdapter(){
                @Override
                public void onTextMessage(WebSocket ws, String message) {
                    System.out.println("Get message ====================================== !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    try {
                        if (!message.contains("{")) {
                            System.err.println("|||" + message + "|||");
                            return;
                        }

                        JSONObject jsonArray = (JSONObject) getArrayFromJSON(message);

                        String command = String.valueOf(jsonArray.get("message"));
                        System.err.println(command);

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
//                                System.out.println(MyPrint.getStringFromEmailAccounts(Mailing.emailAccounts));
//                                sendText(MyPrint.getStringFromEmailAccounts(Mailing.emailAccounts));

                                System.out.println("Enter status");

                                webSocket.flush();

                                System.out.println("start accounts = " + Mailing.emailAccounts.size());

                                System.out.println("Enter status 1");
//                                synchronized (Mailing.emailAccounts)

//                                synchronize(Mailing.emailAccounts) {
                                    ConcurrentHashMap<Integer, EmailAccount> tmpEmailAccounts = new ConcurrentHashMap<>(Mailing.emailAccounts); // (ConcurrentHashMap<Integer, EmailAccount>) Mailing.emailAccounts; // TODO создать дубль
//                                }

                                System.out.println("Enter status 2");

                                String json = getJsonFromMap(tmpEmailAccounts);

                                System.out.println(json);

                                System.out.println("Test 1");

//                                System.out.println("mid accounts = " + Mailing.emailAccounts.size());
//
//                                System.out.println(" json length " + json.length());

                                sendText("json", json);

                                System.out.println("end accounts ");

                                webSocket.flush();

                                System.gc();

                                break;
                            default:
                                System.out.println("Error command - " + command);
                        }
                    } catch (Exception e) {
                        System.err.println("|||" + message + "|||");
                        e.printStackTrace();
                    }

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
            if (webSocket != null) {
                webSocket.flush();
                System.err.println("close WSS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                webSocket.sendClose();
                webSocket.disconnect();
//                webSocket.flush()
//                webSocket.isAutoFlush()


                webSocket.addListener(new WebSocketListener() {
                    @Override
                    public void onStateChanged(WebSocket websocket, WebSocketState newState) {
                        System.err.println("onStateChanged");
                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                        System.err.println("onStateChanged");
                    }

                    @Override
                    public void onConnectError(WebSocket websocket, WebSocketException cause) {
                        System.err.println("onConnectError");
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                        System.err.println("onDisconnected");
                    }

                    @Override
                    public void onFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onFrame");
                    }

                    @Override
                    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onContinuationFrame");
                    }

                    @Override
                    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onTextFrame");
                    }

                    @Override
                    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onBinaryFrame");
                    }

                    @Override
                    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onCloseFrame");
                    }

                    @Override
                    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onPingFrame");
                    }

                    @Override
                    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onPongFrame");
                    }

                    @Override
                    public void onTextMessage(WebSocket websocket, String text) {
                        System.err.println("onTextMessage");
                    }

                    @Override
                    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                        System.err.println("onBinaryMessage");
                    }

                    @Override
                    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onSendingFrame");
                    }

                    @Override
                    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onFrameSent");
                    }

                    @Override
                    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) {
                        System.err.println("onFrameUnsent");
                    }

                    @Override
                    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) {
                        System.err.println("onThreadCreated");
                    }

                    @Override
                    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) {
                        System.err.println("onThreadStarted");
                    }

                    @Override
                    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) {
                        System.err.println("onThreadStopping");
                    }

                    @Override
                    public void onError(WebSocket websocket, WebSocketException cause) {
                        System.err.println("onError");
                    }

                    @Override
                    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) {
                        System.err.println("onFrameError");
                    }

                    @Override
                    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) {
                        System.err.println("onMessageError");
                    }

                    @Override
                    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) {
                        System.err.println("onMessageDecompressionError");
                    }

                    @Override
                    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) {
                        System.err.println("onTextMessageError");
                    }

                    @Override
                    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) {
                        System.err.println("onSendError");
                    }

                    @Override
                    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
                        System.err.println("onUnexpectedError");
                    }

                    @Override
                    public void handleCallbackError(WebSocket websocket, Throwable cause) {
                        System.err.println("handleCallbackError");
                    }

                    @Override
                    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) {
                        System.err.println("onSendingHandshake");
                    }
                });

            }

            restart();

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

    public static void sendText(String subject, String text) {

        if (!subject.equals("json")) return;

//        System.out.println("start sendText ");
//        if (webSocket != null && webSocket.isOpen()) {
        if (webSocket != null) {
//            System.out.println("WSS out: " + text);
//            System.out.println("prep start");
            text = forJSON(text);
//            System.out.println("start send");
            webSocket.sendText("{\"subject\":\"" + subject + "\", \"message\":\"" + text + "\"}");
//            System.out.println("end send");
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

    private static boolean isValidJSON(String jsonStr) {
        try {
            new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    private static Object getArrayFromJSON(String jsonStr) {
        try {
            return new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            return false;
        }
    }

    private String getJsonFromMap(ConcurrentHashMap<Integer, EmailAccount> map) {

        System.out.println("getJsonFromMap start");

        StringBuffer tmpStr = new StringBuffer("{");
//        StringBuilder tmpStr = new StringBuilder("{");

        for (Map.Entry<Integer, EmailAccount> e: map.entrySet()){
            System.out.println("start " + e.getKey());
            System.out.println(e.getKey());
            tmpStr.append("\"").append(e.getKey()).append("\": ").append(e.getValue()).append(",");
            System.out.println("end " + e.getKey());
        }

        tmpStr.substring(0, tmpStr.length() - 1);

        String tmp = tmpStr.substring(0,  tmpStr.length() - 1);

        if (tmp.equals("")) {
            tmp = "{}";
        } else {
            tmp += "}";
        }

        return tmp;
    }

    public static WebSocket getWebSocket() {
        return webSocket;
    }
}
