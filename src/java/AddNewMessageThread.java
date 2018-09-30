import javax.mail.Message;

public class AddNewMessageThread implements Runnable {

    private DB        db;
    private User      user;
    private Message[] messages;

    public AddNewMessageThread(User user, Message[] messages) {
        db = new DB();
        this.user     = user;
        this.messages = messages;
    }

    @Override
    public void run() {
        for (Message message : messages) {
//            System.out.print(" " + db.addEmail(new Email(user, message)) + " ");
            db.addEmail(new Email(user, message));
        }
    }
}
