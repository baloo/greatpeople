package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Mails extends Mailer {

    public static void applied(JobApplication jobApplication) {
        setFrom("jobs+" + jobApplication.uniqueID + "-" + jobApplication.id + "@zenexity.com");
        addRecipient(jobApplication.email);
        setSubject("Thank for your job application @ zenexity!");
        send(jobApplication);
    }
    
}

