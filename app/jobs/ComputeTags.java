package jobs;

import java.io.IOException;

import models.Tags;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;

@Every("10min")
public class ComputeTags extends Job {

    public void doJob() {
        try {
            Tags.allTags();
        } catch (IOException e) {
            Logger.error(e, "ComputeTags: Error calculating tags");
        }
    }

}

