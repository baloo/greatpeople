package jobs;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;

import models.Attachement;

import play.*;
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
			System.out.println("---> " + message.getSubject());
			// message.setFlag(Flag.FLAGGED, true);
			String contentString = "";
			List<Attachement> attachements = new ArrayList<Attachement>();
			if (message.getContent() instanceof String) {
				contentString = (String) message.getContent();
			} else if (message.getContent() instanceof Multipart) {
				Multipart mp = (Multipart) message.getContent();
				for (int j = 0; j < mp.getCount(); j++) {
					Part part = mp.getBodyPart(j);

					String disposition = part.getDisposition();

					if (disposition == null) {
						// Check if plain
						MimeBodyPart mbp = (MimeBodyPart) part;
						if (mbp.isMimeType("text/plain")) {
							contentString += (String) mbp.getContent();
						} else {attachements.add(saveAttachment(part));}
					} else if ((disposition != null)
							&& (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
						// Check if plain
						MimeBodyPart mbp = (MimeBodyPart) part;
						if (mbp.isMimeType("text/plain")) {
							contentString += (String) mbp.getContent();
						} else {attachements.add(saveAttachment(part));}
					}
				}
			}
			Logger.info("content : %s -- attachements (%d)", contentString, attachements.size());

		}

		// Close connection
		folder.close(false);
		store.close();
	}
	private Attachement saveAttachment(Part part) throws Exception, MessagingException, IOException {
		// Special non-attachment cases here of
		// image/gif, text/html, ...
		Attachement attachement = new Attachement();
		attachement.name = decodeName(part.getFileName());
		
		File savefile = File.createTempFile("emailattach", ".atch", Play.getFile("/attachements"));
		attachement.path = savefile.getAbsolutePath();
		IO.write(part.getInputStream(), savefile);
		return attachement;
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
