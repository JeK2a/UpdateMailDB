import javax.mail.Message;

public class AddNewMessageThread implements Runnable {

    private Message[] messages;
    private User user;
    private DB db;


    public AddNewMessageThread(User user, Message[] messages) {
        this.messages = messages;
        this.user = user;
        db = new DB();
    }

    @Override
    public void run() {
        for (Message message : messages) {
            System.err.println(db.addEmail(new Email(user, message)));
        }
    }
}
