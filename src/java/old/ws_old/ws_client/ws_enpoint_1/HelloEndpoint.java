package old.ws_old.ws_client.ws_enpoint_1;

//public class HelloEndpoint extends Endpoint {
//
//    @Override
//    public void onOpen(final Session session, EndpointConfig config) {
//
//        session.addMessageHandler(new MessageHandler.Whole<String>() {
//
//            @Override
//            public void onMessage(String msg) {
//                try {
//                    session.getBasicRemote().sendText("Hello " + msg);
//                } catch (IOException e) { }
//            }
//        });
//    }
//}

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/hello")

public class HelloEndpoint {

    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            session.getBasicRemote().sendText("Hello " + msg);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}