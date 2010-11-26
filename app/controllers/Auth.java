package controllers;

import models.User;
import play.Logger;
import play.libs.OpenID;
import play.mvc.Before;
import play.mvc.Controller;

public class Auth extends Controller {

    static String REMEMBER_ME = "gpuser";
    static String REMEMBER_FOR = "10d";
    static String ZENEXITY_OPENID = "https://www.google.com/accounts/o8/site-xrds?hd=zenexity.com";

    @Before(unless="authenticate")
    static void checkUser() {
        Logger.info("checkUser");
        // Protect
        if (!session.contains("username")) {
            authenticate();
        }
        // Retrieve connected user
        String username = session.get("username");
        User user = User.findOrCreate(username);
        // Now we have the user
        renderArgs.put("user", user);
    }

    public static void logout() {
        session.clear();
        response.removeCookie(REMEMBER_ME);
        Application.index();
    }

    public static void error() {
        renderText("Error logging in");
    }

    public static void authenticate() {
        Logger.info("authenticate()");
        if (OpenID.isAuthenticationResponse()) {
            OpenID.UserInfo verifiedUser = OpenID.getVerifiedID();
            Logger.info("Got response!");
            if (verifiedUser == null) {
                flash.error("user.failed");
                response.removeCookie(REMEMBER_ME);
                error();
            }
            User user = User.findOrCreate(verifiedUser.id);
            session.put("username", user.openid);
            Application.index();
        } else {
            Logger.info("Verify with Zenexity openid");
            if (!OpenID.id(ZENEXITY_OPENID).verify()) {
                response.removeCookie(REMEMBER_ME);
                error();
            }
        }
    }


}
