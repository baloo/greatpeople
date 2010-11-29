package controllers;

import java.util.List;

import org.apache.commons.io.filefilter.NotFileFilter;

import models.JobApplication;
import models.JobApplication.JobStatus;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class) 
public class Application extends Controller {

    public static void index() {
        new jobs.HouseKeeping().now();
        box("new", 0);
    }

    public static void check() throws Exception {
        new jobs.FetchEmails().now().get();
        box("new", 0);
    }

    // API

    public static void box(String boxid, int pageId) {
        JobStatus status = JobStatus.find(boxid);
        if (status == null || status == JobStatus.DELETED) notFound();
        List<JobApplication> applications = JobApplication.find("status = ? order by submitted desc", status)
            .from(JobApplication.PER_PAGE * pageId).fetch(JobApplication.PER_PAGE);
        int pageCount = JobApplication.pageCount(status);
        if (pageId >= pageCount) notFound();
        render(boxid, pageId, applications, pageCount);
    }

    public static void candidate() {
        if (Play.mode.isProd()) notFound();
        render();
    }


}