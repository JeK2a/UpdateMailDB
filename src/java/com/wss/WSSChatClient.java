package com.wss;

import com.Main;
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

public class WSSChatClient {
    private static WebSocket webSocket;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;

    public static boolean result = false;

    private static String json_old = "";

    private final String url = "wss://my.tdfort.ru:10001/?ws_group=worker:javamail";

    public WSSChatClient() {
        try {
            webSocketFactory = new WebSocketFactory();
            webSocketAdapter = new WebSocketAdapter(){
                @Override
                public void onTextMessage(WebSocket ws, String message) {
                    try {
                        if (!message.contains("{")) {
                            System.err.println("============================================================================");
                            System.err.println(message);
                            System.err.println("============================================================================");
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
                            case "status":
//                                System.out.println(MyPrint.getStringFromEmailAccounts(Mailing.emailAccounts));
//                                sendText(MyPrint.getStringFromEmailAccounts(Mailing.emailAccounts));

                                JSONObject json_new = new JSONObject(Mailing.emailAccounts);

                                if (json_old.equals(json_new.toString())) {
                                    sendText("zero", "");
                                } else {
                                    json_old = json_new.toString();
                                    sendText("json", json_new.toString());
                                }

//                                System.out.println(json_new.toString());
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
        if (webSocket != null && webSocket.isOpen()) {
//            System.out.println("WSS out: " + text);
            text = forJSON(text);
            webSocket.sendText("{\"subject\":\"" + subject + "\", \"message\":\"" + text + "\"}");
        } else {
//            System.err.println("WSS out error: " + text);
        }
    }

    public void sendText(StringBuffer stringBuffer) {
        sendText(stringBuffer.toString());
    }

    public void sendText(Object o) {
        sendText(String.valueOf(o));
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
            return  new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            return false;
        }
    }

}
