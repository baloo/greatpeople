package models;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Email;
import play.db.jpa.Model;
import play.utils.Utils;

@Entity
public class JobApplication extends Model {

    public static enum JobStatus {
        NEW, INPROGRESS, ARCHIVED
    }

    public String name;

    @Email
    public String email;

    public String phone;

    // Separated by commas
    public String tags;

    public JobStatus status;

    @OneToMany
    public List<Attachment> attachments;

    public List<String> tagsList() {
        return Arrays.asList(tags.split(","));
    }

    public void changeTags(List<String> tagsList) {
        tags = Utils.join(tagsList, ",");
    }

    @Override
    public String toString() {
        return name + " <" + email + ">";
    }

}
