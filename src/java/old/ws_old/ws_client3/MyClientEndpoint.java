package old.ws_old.ws_client3;

import javax.websocket.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

//@ClientEndpoint
public class MyClientEndpoint extends Endpoint {
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("Connected to endpoint: " + session.getBasicRemote());
//        try {
//            String name = "Duke";
//            System.out.println("Sending message to endpoint: " + name);
//            session.getBasicRemote().sendText(name);
//        } catch (IOException ex) {
//            Logger.getLogger(MyClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

//    @OnMessage
//    public void processMessage(String message) {
//        System.out.println("Received message in client: " + message);
//        Client_1.messageLatch.countDown();
//    }

//    @OnError
//    public void processError(Throwable t) {
//        t.printStackTrace();
//    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        try {
            String name = "Duke";
            System.out.println("Sending message to endpoint: " + name);
            session.getBasicRemote().sendText(name);
        } catch (IOException ex) {
            Logger.getLogger(MyClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}