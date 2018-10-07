package old.ws_old.ws_client3;

//a bare bone implementation of a programmatic endpoint

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;

public class WeatherClient extends Endpoint {
    private Session session;
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        try {
            //sends back the session ID to the peer
            System.out.println("Session");
            this.session.getBasicRemote().sendText("Session ID: " + this.session.getId());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
