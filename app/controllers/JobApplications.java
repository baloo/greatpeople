package controllers;

import models.JobApplication;
import models.Note;
import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class)
public class JobApplications extends Controller {

    public static void index(Long id){
        JobApplication resume = JobApplication.findById(id);
        notFoundIfNull(resume);
        render(resume);
    }
    
    public static void postNote(Long resumeId, String comment, Integer rating) {
        JobApplication resume = JobApplication.findById(resumeId);
        resume.addInternalNote(session.get("name"), session.get("email"), comment, rating);
        index(resumeId);
    }

    public static void change(Long resumeId, String id, String value) {
        JobApplication resume = JobApplication.findById(resumeId);
        notFoundIfNull(resume);
        if ("name".equals(id)) {
            resume.name = value;
        } else if ("email".equals(id)) {
            resume.email = value;
        } else if ("phone".equals(id)) {
            resume.phone = value;
        } else {
            badRequest();
        }
        resume.save();
        renderText(value);
    }

}
