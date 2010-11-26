package controllers;

import java.util.List;

import models.JobApplication;
import models.JobApplication.JobStatus;
import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class) 
public class Application extends Controller {

    public static void index() {
        box("new");
    }

    public static void test() throws Exception {
        new jobs.FetchEmails().now().get();
        renderText("DONE");
    }

    // API

    public static void box(String boxid) {
        List<JobApplication> applications = null;
        if ("new".equals(boxid)) {
            applications = JobApplication.find("status", JobStatus.NEW).fetch();
        } else if ("inprogress".equals(boxid)) {
            applications = JobApplication.find("status", JobStatus.INPROGRESS).fetch();
        } else if ("archived".equals(boxid)) {
            applications = JobApplication.find("status", JobStatus.ARCHIVED).fetch();
        } else {
            notFound();
        }
        
        render(boxid, applications);
    }

    public static void candidate() {
        render();
    }


}