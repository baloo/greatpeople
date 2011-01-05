package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Mails extends Mailer {

    public static void applied(JobApplication jobApplication) {
        setFrom("jobs+" + jobApplication.uniqueID + "-" + jobApplication.id + "@zenexity.com");
        addRecipient(jobApplication.email);
        setSubject("Thank you for being interested by zenexity!");
        send(jobApplication);
    }

    public static void sendMessage(JobApplication jobApplication, String message) {
        setFrom("jobs+" + jobApplication.uniqueID + "-" + jobApplication.id + "@zenexity.com");
        addRecipient(jobApplication.email);
        addBcc("jobs@zenexity.com");
        setSubject("Your job application @ zenexity");
        send(jobApplication, message);
    }

    public static void alert(List oldApplications) {
        setFrom("jobs@zenexity.com");
        addRecipient("jobs@zenexity.com");
        setSubject("There are some job applications to process");
        send(oldApplications);
    }

}

