package models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import play.Logger;
import play.data.validation.Email;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import play.libs.Codec;
import play.modules.search.Field;
import play.modules.search.Indexed;
import play.modules.search.Query;
import play.modules.search.Search;
import play.mvc.Router;
import play.templates.JavaExtensions;
import play.utils.HTML;
import play.utils.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


@Entity
@Indexed
public class JobApplication extends Model {

    public final static int PAGE_SIZE = 10;

    public static enum JobStatus {
        NEW, INPROGRESS, ARCHIVED, HIRED, DELETED;
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
    @Field public Date submitted = new Date();
    public String phone;
    @Lob @Field public String message;
    @Field @Enumerated(EnumType.STRING) public JobStatus status = JobStatus.NEW;
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
        addMessage(from, email, content, null);
    }

    public void addMessage(String from, String email, String content, List<Attachment> attachments) {
        new Note(this, from, email, content, false).save();
        if (attachments != null) {
            for(Attachment attachment : attachments) {
                attachment.jobApplication = this;
                attachment.save();
            }
        }
        this.save();
    }

    public void addInternalNote(String from, String email, String content, Integer rating) {
        Note note = new Note(this, from, email, content, true);
        note.rating = rating;
        note.save();
    }

    /**
     * Strip tags from the message *then* htmlify some text (to protect from XSS)
     * @return an HTML string
     */
    public String htmlMessage() {
        return HTML.htmlEscape(message).replaceAll("(https?://[^\\s]+)", "<a href='$1'>$1</a>");
    }

    public List<Note> getNotes() {
        return Note.find("jobApplication = ? order by date asc", this).fetch();
    }

    public List<String> tagList() {
        if (tags == null || "".equals(tags)) return new ArrayList<String>();
        return Arrays.asList(tags.split("\\s+"));
    }

    public Double getRating() {
        String query = "select avg(rating) "
                     + "from Note where rating > 0 and jobApplication = :appid "
                     + "group by jobApplication";
        javax.persistence.Query indexQuery = JPA.em().createQuery(query);
        indexQuery.setParameter("appid", this);
        @SuppressWarnings("unchecked")
        List<Double> rows = indexQuery.getResultList();
        return rows.size() > 0 ? rows.get(0) : 0;
   }

    public List<Attachment> getAttachments() {
        return Attachment.find("byJobApplication", this).fetch();
    }

    ////////////////////////////////////////////////////////////
    // Search

    private static Query query(JobStatus status, String query) {
        if (query == null || query.length() == 0) {
            return Search.search("status:" + status, JobApplication.class)
                .orderBy("submitted");
        }
        String normalized = normalizeQuery(query, status);
        if (normalized.length() == 0) {
            return null;
        }
        try {
            return Search.search(normalized, JobApplication.class)
                .orderBy("submitted");
        } catch (Exception e) {
            Logger.warn(e, "Error parsing query");
            return null;
        }
    }

    public static List<JobApplication> search(JobStatus status, String query, int page) {
        Query luceneQ = query(status, query);
        if (luceneQ == null) return new ArrayList<JobApplication>();
        return luceneQ.page(page * PAGE_SIZE, PAGE_SIZE).fetch();
    }

    public static int pageCount(JobStatus status, String query) {
        Query luceneQ = query(status, query);
        if (luceneQ == null) return 1;
        return (int)Math.ceil(new Double(luceneQ.count()) / new Double(PAGE_SIZE));
    }

    public static String normalizeQuery(String input, JobStatus status) {
        List<String> predicates = new ArrayList<String>();
        predicates.add("(status:" + status + ")");
        for (String term: input.split("\\s+")) {
            if (term.startsWith("tag:") && term.length() > 6) {
                predicates.add("(tags:" + term.substring("tag:".length()) + ")");
            } else {
                predicates.add("(name:" + term + " OR message:" + term + " OR tags:" + term + ")");
            }
        }
        return Utils.join(predicates, " AND ");
    }

    public String stub() {
        return name.replaceAll("\\s+", "-").replaceAll("[^a-zA-Z0-9-]", "");
    }

    public String url() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("id", this.id);
        args.put("stub", this.stub());
        return Router.getFullUrl("JobApplications.index", args);
    }

    @Override
    public String toString() {
        return name + " <" + email + ">";
    }

    public static class Serializer implements JsonSerializer<JobApplication> {
        public JsonElement serialize(JobApplication jobApplication, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("id", context.serialize(jobApplication.id));
            result.add("url", context.serialize(jobApplication.url()));
            if (jobApplication.name != null) {
                result.add("name", context.serialize(HTML.htmlEscape(jobApplication.name)));
            }
            if (jobApplication.email != null) {
                result.add("email", context.serialize(HTML.htmlEscape(jobApplication.email)));
            }
            if (jobApplication.submitted != null) {
                result.add("submitted", context.serialize(jobApplication.submitted.getTime()));
                String formattedDate = JavaExtensions.format(jobApplication.submitted, "dd MMMM yyyy hh:mm:ss");
                result.add("formattedDate", context.serialize(formattedDate));
            }
            if (jobApplication.phone != null) {
                result.add("phone", context.serialize(HTML.htmlEscape(jobApplication.phone)));
            }
            result.add("tags", context.serialize((jobApplication.tagList())));
            result.add("rating", context.serialize(jobApplication.getRating()));
            return result;
        }
    }

}
