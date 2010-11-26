package models;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Email;
import play.db.jpa.Model;
import play.utils.Utils;

@Entity
public class Resume extends Model {

    public static final int STATUS_NEW = 0;
    public static final int STATUS_INPROGRESS = 2;
    public static final int STATUS_ARCHIVED = 3;

    public String name;

    @Email
    public String email;

    public String phone;

    // Separated by commas
    public String tags;

    public int status;

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
