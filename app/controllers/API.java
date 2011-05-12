package controllers;

import java.util.List;

import models.JobApplication;
import models.JobApplication.JobStatus;
import play.mvc.Before;
import play.mvc.Controller;

public class API extends Controller {

    public static void applications(String boxid, int p, String q) {
        JobStatus status = JobStatus.find(boxid);
        int pageId = p < 1 ? 0 : p - 1; // The API starts at 1 but internally we start at 0
        if (status == null || status == JobStatus.DELETED) notFound();
        List<JobApplication> applications = JobApplication.search(status, q, pageId);
        int pageCount = JobApplication.pageCount(status, q);
        if (pageId > pageCount) notFound();

        renderJSON(new ApplicationResult(applications, pageId, pageCount), new JobApplication.Serializer());
    }

    // We don't want to redirect to the login page, so we don't use @With(Auth.class)
    @Before
    static void checkUser() {
        // Protect
        if (!session.contains("email")) {
            forbidden();
        }
    }

    // Useful for json serialization
    public static class ApplicationResult {
        public List<JobApplication> applications;
        public int pageNumber;
        public int pageCount;
        public ApplicationResult(List<JobApplication> applications, int pageNumber, int count) {
            this.applications = applications;
            this.pageNumber = pageNumber;
            this.pageCount = count;
        }
    }

}
