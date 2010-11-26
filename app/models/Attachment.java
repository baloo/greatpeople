package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Attachment extends Model {

    public String name;

    public Blob content = new Blob();

}
