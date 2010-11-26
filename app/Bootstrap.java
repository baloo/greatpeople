import models.JobApplication;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        if(Play.mode == Play.Mode.DEV) {
            if (JobApplication.count() == 0) {
                Fixtures.load("bootstrap/initialdata.yml");
            }
        }
    }

}

