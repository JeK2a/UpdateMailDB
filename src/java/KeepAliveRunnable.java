//import com.sun.mail.imap.IMAPFolder;
//
//private static class KeepAliveRunnable implements Runnable {
//
//    private static final long KEEP_ALIVE_FREQ = 300000; // 5 minutes
//
//    private IMAPFolder folder;
//
//    public KeepAliveRunnable(IMAPFolder folder) {
//        this.folder = folder;
//    }
//
//    @Override
//    public void run() {
//        while (!Thread.interrupted()) {
//            try {
//                Thread.sleep(KEEP_ALIVE_FREQ);
//
//                // Perform a NOOP just to keep alive the connection
//                LOGGER.debug("Performing a NOOP to keep alvie the connection");
//                folder.doCommand(new IMAPFolder.ProtocolCommand() {
//                    public Object doCommand(IMAPProtocol p)
//                            throws ProtocolException {
//                        p.simpleCommand("NOOP", null);
//                        return null;
//                    }
//                });
//            } catch (InterruptedException e) {
//                // Ignore, just aborting the thread...
//            } catch (MessagingException e) {
//                // Shouldn't really happen...
//                LOGGER.warn("Unexpected exception while keeping alive the IDLE connection", e);
//            }
//        }
//    }
//}