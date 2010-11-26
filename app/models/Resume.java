package models;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Resume extends Model {

    public String name;

    @Email
    public String email;

    public String phone;

    public Blob attachment;


}
