package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.libs.OpenID;
import play.db.jpa.Model;
import play.utils.Utils;

@Entity
public class Note extends Model {

    public String comment;
    public String name;
    public String email;
    public Integer rating;
    
    @ManyToOne
    public JobApplication jobApplication;
    
    @Override
    public String toString() {
        return comment + " (" + name + ")";
    }
}
