package models.json;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import models.JobApplication;
import play.libs.I18N;
import play.templates.JavaExtensions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JobApplicationSerializer implements JsonSerializer<JobApplication> {

    @Override
    public JsonElement serialize(JobApplication jobApplication, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", new JsonPrimitive(jobApplication.id));
        result.add("name", new JsonPrimitive(jobApplication.name));
        result.add("email", new JsonPrimitive(jobApplication.email));
        result.add("submitted", new JsonPrimitive(jobApplication.submitted.getTime()));
        if (jobApplication.submitted != null) {
            String formattedDate = JavaExtensions.format(jobApplication.submitted, "dd MMMM yyyy hh:mm:ss");
            result.add("formattedDate", new JsonPrimitive(formattedDate));
        }
        result.add("phone", new JsonPrimitive(jobApplication.phone));
        result.add("rating", new JsonPrimitive(jobApplication.getRating()));
        return result;
    }

}
