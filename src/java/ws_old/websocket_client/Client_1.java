//package ws_old.websocket_client;
//
//import org.glassfish.tyrus.core.websocket.ContainerProvider;
//
//import javax.websocket.DeploymentException;
//import javax.websocket.WebSocketContainer;
//import java.io.IOException;
//import java.net.URI;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class Client_1 {
//
//    final static CountDownLatch messageLatch = new CountDownLatch(1);
//
//    public static void main(String[] args) {
//        try {
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            String url = "wss://my.tdfort.ru:8897";
//            System.out.println("Connecting to " + url);
//            container.connectToServer(MyClientEndpoint.class, URI.create(url));
//            messageLatch.await(100, TimeUnit.SECONDS);
//        } catch (DeploymentException | InterruptedException | IOException ex) {
//            Logger.getLogger(Client_1.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}