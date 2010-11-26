package controllers;

import play.mvc.Controller;
import play.mvc.With;

import models.JobApplication;
import models.Note;

@With(Auth.class)
public class JobApplications extends Controller {
    
    public static void index(Long id){
        JobApplication resume = JobApplication.findById(id);
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
}