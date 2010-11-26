package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import play.data.validation.Email;
import play.db.jpa.Model;
import play.utils.Utils;
import play.libs.Codec;


@Entity
public class JobApplication extends Model {

    public static enum JobStatus {
        NEW, INPROGRESS, ARCHIVED
    }

    public String name;
    @Email public String email;
    public Date submitted = new Date();
    public String phone;    
    @Lob public String message;
    @Enumerated(EnumType.STRING) public JobStatus status = JobStatus.NEW;    
    public String uniqueID;

    public JobApplication(String name, String email, String message, List<Attachment> attachments) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.uniqueID = Codec.UUID().substring(0,5);
    }

    public void addMessage(String from, String email, String content) {
        new Note(this, from, email, content, false).create();
    }
    
    public void addInternalNote(String from, String email, String content) {
        new Note(this, from, email, content, true).create();
    }
    
    public List<Note> getNotes() {
        return Note.find("jobApplication = ? order by date desc", this).fetch();
    }

    @Override
    public String toString() {
        return name + " <" + email + ">";
    }

}
