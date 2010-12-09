package models;

import javax.persistence.Entity;
import javax.persistence.Lob;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

@Entity
public class AnswerTemplate extends Model {
    public String label;
    @Lob
    @MaxSize(1000)
    public String text;

    @Override
    public String toString() {
        return label;
    }

}

