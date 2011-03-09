package controllers;
import org.yaml.snakeyaml.Yaml;

import models.Attachment;
import models.JobApplication;
import models.JobApplication.JobStatus;
import models.*;
import play.Play;
import play.cache.Cache;
import play.libs.IO;
import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class)
public class JobApplications extends Application {

    public static void index(Long id){
        JobApplication resume = JobApplication.findById(id);
        notFoundIfNull(resume);
        Object templates = Cache.get("answer_templates");
        if (templates == null){
        	templates = AnswerTemplate.all().fetch();
        	if (Play.mode == Play.Mode.PROD) Cache.add("answer_templates", templates, "1h");
        }
        render(resume, templates);
    }

    public static void postNote(Long resumeId, String comment, Integer rating, String status) {
        JobApplication resume = JobApplication.findById(resumeId);
        if (resume == null) badRequest();
        JobStatus jobStatus = JobStatus.find(status);
        resume.status = jobStatus != null ? jobStatus : JobStatus.INPROGRESS;
        resume.save();
        resume.addInternalNote(session.get("name"), session.get("email"), comment, rating);
        index(resumeId);
    }

    public static void sendMessage(Long resumeId, String comment) {
        JobApplication resume = JobApplication.findById(resumeId);
        if (resume == null) badRequest();
        resume.status = JobApplication.JobStatus.INPROGRESS;
        resume.save();
        resume.addMessage(session.get("name"), session.get("email"), comment);
        Mails.sendMessage(resume, comment);
        index(resumeId);
    }

    // Filename is not used, it's just to get the correct filename at download time
    public static void download(Long id, String filename) {
        Attachment attachment = Attachment.findById(id);
        notFoundIfNull(attachment);
        response.contentType = attachment.content.type();
        renderBinary(attachment.content.getFile());
    }

    public static void delete(Long id) throws Exception {
        JobApplication resume = JobApplication.findById(id);
        if (resume == null) badRequest();
        resume.status = JobApplication.JobStatus.DELETED;
        resume.save();
        Application.index();
    }

    public static void change(Long resumeId, String id, String value) {
        JobApplication resume = JobApplication.findById(resumeId);
        if (resume == null) badRequest();
        if ("name".equals(id)) {
            resume.name = value;
        } else if ("email".equals(id)) {
            resume.email = value;
        } else if ("phone".equals(id)) {
            resume.phone = value;
        } else if ("tags".equals(id)) {
            resume.tags = value;
        } else {
            badRequest();
        }
        resume.save();
        renderText(value);
    }
}
