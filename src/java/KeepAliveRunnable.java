import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;

import javax.mail.Folder;
import javax.mail.MessagingException;
import java.net.ProtocolException;

public class KeepAliveRunnable implements Runnable {

//    private static final long KEEP_ALIVE_FREQ = 300000; // 5 minutes
    private static final long KEEP_ALIVE_FREQ = 3000; // 5 minutes

    private IMAPFolder folder;
    private int number;

    public KeepAliveRunnable(IMAPFolder folder, int number) {
        this.folder = folder;
        this.number = number;
    }

    @Override
    public void run() {
        System.out.println(number + " Thread started");

        while (true) {
            try {
                Thread.sleep(KEEP_ALIVE_FREQ);
                System.out.println(
                        number + " - "
                                + folder.getUnreadMessageCount()
                                + "/" + folder.getMessageCount()
                );
            } catch (MessagingException | InterruptedException e) {
                e.printStackTrace();
            }
        }

//        while (!Thread.interrupted()) {
//
//
//            try {
////                Thread.sleep(KEEP_ALIVE_FREQ);
//
//
//                // Perform a NOOP just to keep alive the connection
////                LOGGER.debug("Performing a NOOP to keep alvie the connection");
//                folder.doCommand(protocol -> {
//                    try {
//                        protocol.simpleCommand("NOOP", null);
//                        System.err.println(number + " - " + folder.getUnreadMessageCount()
//                                           + "/" + folder.getMessageCount());
//                    } catch (com.sun.mail.iap.ProtocolException | MessagingException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                });
//            } catch (InterruptedException e) {
//                // Ignore, just aborting the thread...
//            } catch (MessagingException e) {
//                // Shouldn't really happen...
////                LOGGER.warn("Unexpected exception while keeping alive the IDLE connection", e);
//            }

    }
}
