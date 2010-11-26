package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class User extends Model {

    public String openid;

    public String name;

    public User(String openid, String name) {
        this.openid = openid;
        this.name = name;
    }

    public static User findOrCreate(String openid) {
        User u = find("openid", openid).first();
        if (u == null) {
            u = new User(openid, openid);
            u.save();
        }
        return u;
    }

}
