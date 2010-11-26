package jobs;

import java.util.*;
import javax.mail.*;
import javax.mail.search.*;

import play.*;
import play.jobs.*;

public class FetchEmails extends Job {

    public void doJob() throws Exception {

        // Connect to gmail mailbox
        Properties props = new Properties();
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.port", "993");
        Session session = Session.getDefaultInstance(props);
        Store store = session.getStore("imap");
        store.connect("imap.gmail.com", Play.configuration.getProperty("mailbox.username"), Play.configuration.getProperty("mailbox.password"));

        // Open jobs mailbox
        Folder folder = store.getFolder("jobs");
        folder.open(Folder.READ_WRITE);

        // Search unstarred messages
        SearchTerm unstarred = new FlagTerm(new Flags(Flags.Flag.FLAGGED), false);
        Message[] messages = folder.search(unstarred);

        for (Message message : messages) {
            System.out.println("---> " + message.getSubject());
            //message.setFlag(Flag.FLAGGED, true);
            message.getDisposition();
        }

        // Close connection
        folder.close(false);
        store.close();
    }
    
}

