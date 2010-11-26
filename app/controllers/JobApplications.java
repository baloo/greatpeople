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
        
        Note note = new Note();
        note.jobApplication = resume;
        note.comment = comment;
        note.rating = rating;
        note.name = session.get("name");
        note.email = session.get("email");
        note.save();
        
        index(resumeId);
    }

    public static void change(Long resumeId, String id, String value) {
        JobApplication resume = JobApplication.findById(resumeId);
        notFoundIfNull(resume);
        if ("name".equals(id)) {
            resume.name = value;
        } else if ("email".equals(id)) {
            resume.email = value;
        } else {
            badRequest();
        }
        resume.save();
        renderText(value);
    }

}
