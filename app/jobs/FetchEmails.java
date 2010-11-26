package jobs;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.*;
import javax.mail.search.*;

import models.Attachment;
import models.JobApplication;

import play.*;
import play.db.jpa.Blob;
import play.jobs.*;
import play.libs.IO;

public class FetchEmails extends Job {

  public void doJob() throws Exception {

    // Connect to gmail mailbox
    Properties props = new Properties();
    props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.setProperty("mail.imap.socketFactory.port", "993");
    Session session = Session.getDefaultInstance(props);
    Store store = session.getStore("imap");
    store.connect("imap.gmail.com", Play.configuration.getProperty("mailbox.username"), Play.configuration
        .getProperty("mailbox.password"));

    // Open jobs mailbox
    Folder folder = store.getFolder("jobs");
    folder.open(Folder.READ_WRITE);

    // Search unstarred messages
    SearchTerm unstarred = new FlagTerm(new Flags(Flags.Flag.FLAGGED), false);
    Message[] messages = folder.search(unstarred);
    
    for (Message message : messages) {
      
      
      String contentString = "";
      
      List<Attachment> attachments = new ArrayList<Attachment>();
      if (message.getContent() instanceof String) {
        contentString = (String) message.getContent();
      } else if (message.getContent() instanceof Multipart) {
        Multipart mp = (Multipart) message.getContent();
        for (int j = 0; j < mp.getCount(); j++) {
          Part part = mp.getBodyPart(j);

          String disposition = part.getDisposition();

          if (disposition == null || 
              ((disposition != null) && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE)))
            ) {
            // Check if plain
            MimeBodyPart mbp = (MimeBodyPart) part;
            if (mbp.isMimeType("text/plain")) {
              contentString += (String) mbp.getContent();
            } else {
              attachments.add(saveAttachment(part));
            }
            }
          }
      }
      Logger.info("content : %s -- attachements (%d)", contentString, attachments.size());
      String name = ((InternetAddress) message.getFrom()[0]).getPersonal();
      String email =  ((InternetAddress) message.getFrom()[0]).getAddress();
      JobApplication application = new JobApplication(name, email, contentString, attachments);
      application.save();
      if (Play.mode == Play.Mode.PROD) {
        message.setFlag(Flag.FLAGGED, true);
      }
    }

    // Close connection
    folder.close(false);
    store.close();
  }
  private Attachment saveAttachment(Part part) throws Exception, MessagingException, IOException {
    // Special non-attachment cases here of
    // image/gif, text/html, ...
    Attachment attachment = new Attachment();
    attachment.name = decodeName(part.getFileName());
    attachment.content.set(part.getInputStream(), part.getContentType());
    attachment.save();
    return attachment;
  }
  protected String decodeName(String name) throws Exception {
    if (name == null || name.length() == 0) {
      return "unknown";
    }
    String ret = java.net.URLDecoder.decode(name, "UTF-8");

    // also check for a few other things in the string:
    ret = ret.replaceAll("=\\?utf-8\\?q\\?", "");
    ret = ret.replaceAll("\\?=", "");
    ret = ret.replaceAll("=20", " ");

    return ret;
  }

}
