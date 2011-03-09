package jobs;

import java.util.Calendar;
import java.util.List;

import models.JobApplication;
import models.JobApplication.JobStatus;
import play.jobs.Every;
import play.jobs.Job;
import controllers.Mails;

@Every("1h")
public class HouseKeeping extends Job {

    public void doJob() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -7);
        List old = JobApplication.find("submitted < ? and status = ?", now.getTime(), JobStatus.NEW).fetch();
        if(!old.isEmpty()) {
            Mails.alert(old);
        }
    }

}

