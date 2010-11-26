package models;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.utils.Utils;

@Entity
public class Resume extends Model {

    public String name;

    @Email
    public String email;

    public String phone;

    // Separated by commas
    public String tags;

    public Blob attachment;

    public List<String> tagsList() {
        return Arrays.asList(tags.split(","));
    }

    public void changeTags(List<String> tagsList) {
        tags = Utils.join(tagsList, ",");
    }

}
