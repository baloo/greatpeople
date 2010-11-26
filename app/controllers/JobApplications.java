package controllers;

import play.mvc.Controller;
import play.mvc.With;

import models.JobApplication;

@With(Auth.class)
public class JobApplications extends Controller {
    
    public static void index(Long id){
        JobApplication resume = JobApplication.findById(id);
        render(resume);
    }
}