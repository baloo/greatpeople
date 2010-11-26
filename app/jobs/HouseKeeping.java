package jobs;

import java.util.*;

import play.jobs.*;

import models.*;
import controllers.*;

@Every("24h")
public class HouseKeeping extends Job {

    public void doJob() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -7);
        List old = JobApplication.find("submitted < ?", now.getTime()).fetch();
        if(!old.isEmpty()) {
            Mails.alert(old);
        }
    }
    
}

