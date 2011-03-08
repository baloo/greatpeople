package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Lob;

import play.libs.OpenID;
import play.db.jpa.Model;
import play.utils.Utils;

@Entity
public class Note extends Model {

    @ManyToOne public JobApplication jobApplication;
    public Date date = new Date();
    @Lob public String comment;
    public String name;
    public String email;
    public Integer rating;
    public boolean internal;

    public Note(JobApplication jobApplication, String name, String email, String comment, boolean internal) {
        this.jobApplication = jobApplication;
        this.name = (name != null && name.length() > 0) ? name : "NoName";
        this.email = email;
        this.comment = comment;
        this.internal = internal;
    }

    @Override
    public String toString() {
        String summary;
        if (comment == null || comment.length() == 0) {
            summary = "empty";
        } else if (comment.length() <= 50) {
           summary = comment;
        } else {
            summary = comment.substring(0, 50) + " [...]";
        }
        return name + ": " + summary;
    }
}
