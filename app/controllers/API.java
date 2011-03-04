package controllers;

import java.util.List;

import models.JobApplication;
import models.JobApplication.JobStatus;
import models.json.JobApplicationSerializer;
import play.mvc.Before;
import play.mvc.Controller;

public class API extends Controller {

    public static void applications(String boxid, int pageId, String q) {
        JobStatus status = JobStatus.find(boxid);
        if (status == null || status == JobStatus.DELETED) notFound();
        List<JobApplication> applications = JobApplication.search(status, q)
            .from(JobApplication.PER_PAGE * pageId).fetch(JobApplication.PER_PAGE);
        int pageCount = JobApplication.pageCount(status);
        if (pageId >= pageCount) notFound();
        renderJSON(applications, new JobApplicationSerializer());
    }

    // We don't want to redirect to the login page, so we don't use @With(Auth.class)
    @Before
    static void checkUser() {
        // Protect
        if (!session.contains("email")) {
            forbidden();
        }
    }

}
