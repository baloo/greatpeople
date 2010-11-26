package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class)
public class JobApplications extends Controller {
    
    public static void index(){
        render();
    }
}