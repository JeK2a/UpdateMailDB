package chat_connector;

public class ChatConnector implements  Runnable {
    public static void main(String[] args) {
//        String url = "wss://my.tdfort.ru:8897";
//
//        WebSocketFactory webSocketFactory = new WebSocketFactory();
//
//        WebSocketAdapter webSocketAdapter = new WebSocketAdapter(){
//            @Override
//            public void onTextMessage(WebSocket ws, String message) {
//                System.out.println(message);
////                        ws.disconnect();
//            }
//        };
//
//        WebSocket webSocket = null;
//        try {
//            webSocket = webSocketFactory.createSocket(url);
//            webSocket.addListener(webSocketAdapter);
//            webSocket.connect();
//            webSocket.sendText("{\"act\":\"start\",\"user_id\":\"1000\",\"user_name\":\"Mailler\",\"msg\":\"Подключение установлено обоюдно, отлично!\"}");
//
//            while (true) {
//                webSocket.sendText("{\"act\":\"msg\", \"msg\":\"Mailler\", \"room_id\":\"1000\"}");
//                Thread.sleep(10000);
//            }
//        } catch (IOException | InterruptedException | WebSocketException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void run() {
//        String url = "wss://my.tdfort.ru:8897";
//
//        WebSocketFactory webSocketFactory = new WebSocketFactory();
//
//        WebSocketAdapter webSocketAdapter = new WebSocketAdapter(){
//            @Override
//            public void onTextMessage(WebSocket ws, String message) {
//                System.out.println(message);
////                        ws.disconnect();
//            }
//        };
//
//        WebSocket webSocket = null;
//        try {
//            webSocket = webSocketFactory.createSocket(url);
//            webSocket.addListener(webSocketAdapter);
//            webSocket.connect();
//            webSocket.sendText("{\"act\":\"start\",\"user_id\":\"1000\",\"user_name\":\"Mailler\",\"msg\":\"Подключение установлено обоюдно, отлично!\"}");
//
//            while (true) {
//                webSocket.sendText("{\"act\":\"msg\", \"msg\":\"Mailler\", \"room_id\":\"1000\"}");
//                Thread.sleep(10000);
//            }
//        } catch (IOException | InterruptedException | WebSocketException e) {
//            e.printStackTrace();
//        }
    }
}
