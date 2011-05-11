package models.json;

public class JobApplicationSerializerLite implements com.google.gson.JsonSerializer<models.JobApplication> {

    public com.google.gson.JsonElement serialize(models.JobApplication jobApplication, java.lang.reflect.Type type, com.google.gson.JsonSerializationContext context) {
        com.google.gson.JsonObject result = new com.google.gson.JsonObject();
        result.add("id", context.serialize(jobApplication.id));
        if (jobApplication.name != null) {
            result.add("name", context.serialize(jobApplication.name));
        }
        if (jobApplication.email != null) {
            result.add("email", context.serialize(jobApplication.email));
        }
        if (jobApplication.submitted != null) {
            result.add("submitted", context.serialize(jobApplication.submitted.getTime()));
            String formattedDate = play.templates.JavaExtensions.format(jobApplication.submitted, "dd MMMM yyyy hh:mm:ss");
            result.add("formattedDate", context.serialize(formattedDate));
        }
        if (jobApplication.phone != null) {
            result.add("phone", context.serialize(jobApplication.phone));
        }
        result.add("tags", context.serialize((jobApplication.tagList())));
        result.add("rating", context.serialize(0));
        return result;
    }

}
