package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import org.apache.lucene.index.TermEnum;

import play.Logger;
import play.data.validation.Email;
import play.db.jpa.Model;
import play.libs.Codec;
import play.modules.search.Field;
import play.modules.search.Indexed;
import play.modules.search.Search;
import play.utils.Utils;


@Entity
@Indexed
public class JobApplication extends Model {

    public final static int PER_PAGE = 10;

    public static enum JobStatus {
        NEW, INPROGRESS, ARCHIVED, DELETED;
        public static JobStatus find(String label) {
            String upper = label.toUpperCase();
            for (JobStatus status: values()) {
                if (upper.equals(status.toString())) return status;
            }
            return null;
        }
    }

    @Field public String name;
    @Email @Field public String email;
    public Date submitted = new Date();
    public String phone;
    @Lob @Field public String message;
    @Enumerated(EnumType.STRING) public JobStatus status = JobStatus.NEW;
    public String uniqueID;
    /* Separated by spaces */
    @Field public String tags;

    public JobApplication(String name, String email, String message) {
        this.name = (name != null && name.length() > 0) ? name : "NoName";
        this.email = email;
        this.message = message;
        this.uniqueID = Codec.UUID().substring(0,5);
    }

    public static JobApplication createApplication(String name, String email, String message, List<Attachment> attachments) {
        JobApplication application = new JobApplication(name, email, message);
        application.save();
        for(Attachment attachment : attachments) {
            attachment.jobApplication = application;
            attachment.save();
        }
        return application;
    }

    public void addMessage(String from, String email, String content) {
        new Note(this, from, email, content, false).save();
        this.save();
    }

    public void addInternalNote(String from, String email, String content, Integer rating) {
        Note note = new Note(this, from, email, content, true);
        note.rating = rating;
        note.save();
    }

    public List<Note> getNotes() {
        return Note.find("jobApplication = ? order by date asc", this).fetch();
    }

    public Float getRating() {
        List<Note> notes = getNotes();
        int votes = 0;
        int total = 0;
        for (Note note: notes) {
            if (note.rating != null && note.rating > 0) {
                votes++;
                total += note.rating;
            }
        }
        return votes == 0 ? 0 : (float)total / (float)votes;
    }

    public List<Attachment> getAttachments() {
        return Attachment.find("byJobApplication", this).fetch();
    }

    public static JPAQuery search(JobStatus status, String query) {
        if (query == null || query.length() == 0) {
            return find("status = ? order by submitted desc", status);
        }
        List<Long> ids = Search.search(normalizeQuery(query), JobApplication.class).fetchIds();
        if (ids.size() == 0) {
            return find("id = ? and id = ?", 0L, 1L); // Hack to get a query with an empty result
        }
        JPAQuery q = find("status = ? and id in (:ids) order by submitted desc", status);
        q.bind("ids", ids);
        return q;
    }

    public static int pageCount(JobStatus status, String query) {
        int count;
        if (query == null || query.length() == 0) {
            count = find("status", status).fetch().size();
        } else {
            count = find("status = ? and name like ?", status, "%" + query + "%").fetch().size();
        }
        int result = (int) Math.floor(count / PER_PAGE);
        return result + 1;
    }

    public static String normalizeQuery(String input) {
        List<String> predicates = new ArrayList<String>();
        for (String term: input.split("\\s+")) {
            predicates.add("(name:" + term + " OR message:" + term + " OR tags:" + term + ")");
        }
        return Utils.join(predicates, " AND ");
    }

    @Override
    public String toString() {
        return name + " <" + email + ">";
    }

}
