import com.sun.mail.imap.IMAPFolder;

import java.sql.Timestamp;
import java.util.Objects;

public class MyFolder {
    private String folder_name;
    private Thread thread;
    private String status;
    private Timestamp last_event_time;
    private int event_counter;
    private IMAPFolder imap_folder;

    public MyFolder(String folder_name, Thread thread, String status, Timestamp last_event_time, int event_counter, IMAPFolder imap_folder) {
        this.folder_name = folder_name;
        this.thread = thread;
        this.status = status;
        this.last_event_time = last_event_time;
        this.event_counter = event_counter;
        this.imap_folder = imap_folder;
    }

    public MyFolder(IMAPFolder imap_folder) {
        this.folder_name = imap_folder.getFullName();
        this.status = "new";
        this.last_event_time = null;
        this.event_counter = 0;
        this.imap_folder = imap_folder;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLast_event_time() {
        return last_event_time;
    }

    public void setLast_event_time(Timestamp last_event_time) {
        this.last_event_time = last_event_time;
    }

    public int getEvent_counter() {
        return event_counter;
    }

    public void setEvent_counter(int event_counter) {
        this.event_counter = event_counter;
    }

    public IMAPFolder getImap_folder() {
        return imap_folder;
    }

    public void setImap_folder(IMAPFolder imap_folder) {
        this.imap_folder = imap_folder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFolder myFolder = (MyFolder) o;
        return Objects.equals(folder_name, myFolder.folder_name) &&
               Objects.equals(thread, myFolder.thread) &&
               Objects.equals(imap_folder, myFolder.imap_folder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folder_name, thread, imap_folder);
    }

    @Override
    public String toString() {
        return "MyFolder{" +
                "folder_name='" + folder_name + '\'' +
                ", thread=" + thread +
                ", status='" + status + '\'' +
                ", last_event_time=" + last_event_time +
                ", event_counter=" + event_counter +
                ", imap_folder=" + imap_folder +
                '}';
    }
}
