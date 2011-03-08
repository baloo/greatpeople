package controllers;

import models.JobApplication;
import play.Play;
import play.cache.CacheFor;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class) 
public class Application extends Controller {

    @Before
    static void data() {
        renderArgs.put("newCount", JobApplication.count("status = ?", JobApplication.JobStatus.NEW));
        renderArgs.put("inprogressCount", JobApplication.count("status = ?", JobApplication.JobStatus.INPROGRESS));
        renderArgs.put("archivedCount", JobApplication.count("status = ?", JobApplication.JobStatus.ARCHIVED));
    }

    public static void check() throws Exception {
        new jobs.FetchEmails().now().get();
        index();
    }

    // Pages

    public static void index() {
        render();
    }

}
