package models;

import javax.persistence.Entity;

import play.libs.OpenID;
import play.db.jpa.Model;
import play.utils.Utils;

@Entity
public class Note extends Model {

    public String comment;
    public String name;
    public String email;
    
    @Override
    public String toString() {
        return comment + " (" + name + ")";
    }

}
