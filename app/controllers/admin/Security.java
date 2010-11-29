package controllers.admin;

import play.Play;
import play.Play.Mode;
import controllers.Secure;

public class Security extends Secure.Security {

    public static final String LOGINKEY = "admin.login";
    public static final String PASSWDKEY = "admin.password";

    static boolean authentify(String username, String password) {
        if (Play.mode.isProd()) {
            return  username.equals(Play.configuration.getProperty("admin.user", "admin"))
                && password.equals(Play.configuration.getProperty("admin.password", "admin"));
        }
        return username != null && username.equals(password);
    }

}
