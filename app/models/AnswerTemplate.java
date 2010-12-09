package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class AnswerTemplate extends Model {
    public String label;
    @MaxSize(1000)
    public String text;
}

