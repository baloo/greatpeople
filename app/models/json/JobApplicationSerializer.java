package models.json;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import models.JobApplication;
import play.libs.I18N;
import play.templates.JavaExtensions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JobApplicationSerializer implements JsonSerializer<JobApplication> {

    @Override
    public JsonElement serialize(JobApplication jobApplication, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", context.serialize(jobApplication.id));
        if (jobApplication.name != null) {
            result.add("name", context.serialize(jobApplication.name));
        }
        if (jobApplication.email != null) {
            result.add("email", context.serialize(jobApplication.email));
        }
        if (jobApplication.submitted != null) {
            result.add("submitted", context.serialize(jobApplication.submitted.getTime()));
            String formattedDate = JavaExtensions.format(jobApplication.submitted, "dd MMMM yyyy hh:mm:ss");
            result.add("formattedDate", context.serialize(formattedDate));
        }
        if (jobApplication.phone != null) {
            result.add("phone", context.serialize(jobApplication.phone));
        }
        result.add("tags", context.serialize((jobApplication.tagList())));
        result.add("rating", context.serialize(jobApplication.getRating()));
        return result;
    }

}
