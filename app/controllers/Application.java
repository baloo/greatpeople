package controllers;

import java.util.List;

import models.JobApplication;
import models.JobApplication.JobStatus;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class) 
public class Application extends Controller {

    public static void index() {
        box("new", 0);
    }

    public static void check() throws Exception {
        new jobs.FetchEmails().now().get();
        box();
    }

    // API

    public static void box(String boxid, int pageId) {
        JobStatus status = null;
        if ("new".equals(boxid)) {
            status = JobStatus.NEW;
        } else if ("inprogress".equals(boxid)) {
            status = JobStatus.INPROGRESS;
        } else if ("archived".equals(boxid)) {
            status = JobStatus.ARCHIVED;
        } else {
            notFound();
        }
        List<JobApplication> applications = JobApplication.find("status = ? order by submitted desc", status)
            .from(JobApplication.PER_PAGE * pageId).fetch(JobApplication.PER_PAGE);
        int pageCount = JobApplication.pageCount(status);
        Logger.info("From: " + JobApplication.PER_PAGE * pageId + " fetch " + JobApplication.PER_PAGE);
        render(boxid, pageId, applications, pageCount);
    }

    public static void candidate() {
        render();
    }


}