package old.ws_old.ws_client4;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.net.URI;

public class ChatBot {

    public static void main(String[] args) throws Exception {
//        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI("ws://localhost:8080/jee7-websocket-api/chat"));
        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI("wsw://my.tdfort.ru:8897"));
        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
            public void handleMessage(String message) {
                JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
                String userName = jsonObject.getString("user");
                if (!"bot".equals(userName)) {
                    clientEndPoint.sendMessage(getMessage("Hello " + userName +", How are you?"));
                    // other dirty bot logic goes here.. :)
                }
            }
        });

        while (true) {
            clientEndPoint.sendMessage(getMessage("Hi There!!"));
            Thread.sleep(30000);
        }
    }

    private static String getMessage(String message) {
        return Json.createObjectBuilder()
                .add("user", "bot")
                .add("message", message)
                .build()
                .toString();
    }
}