package ws_client3;

//annotated client endpoint in action

import javax.websocket.*;

@ClientEndpoint
public class AnnotatedChatClient {

    private ClientEndpointConfig clientConfig;
    private String user;
    @OnOpen
    public void connected(Session session, EndpointConfig clientConfig){
        this.clientConfig = (ClientEndpointConfig) clientConfig;
        this.user = session.getUserPrincipal().getName();
        System.out.println("User " + user + " connected to Chat room");
    }
    @OnMessage
    public void connected(String msg){
        System.out.println("Message from chat server: " + msg);
    }
    @OnClose
    public void disconnected(Session session, CloseReason reason){
        System.out.println("User "+ user + " disconnected as a result of "+ reason.getReasonPhrase());
    }
    @OnError
    public void disconnected(Session session, Throwable error){
        System.out.println("Error communicating with server: " + error.getMessage());
    }

//    @Override
//    public void onOpen(Session session, EndpointConfig config) {
//        System.out.println("Connect");
//    }
}