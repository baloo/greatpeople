package controllers;

import play.Logger;
import play.libs.OpenID;
import play.mvc.Before;
import play.mvc.Controller;

public class Auth extends Controller {

    static String ZENEXITY_OPENID = "https://www.google.com/accounts/o8/site-xrds?hd=zenexity.com";

    @Before(unless="authenticate")
    static void checkUser() {
        // Protect
        if (!session.contains("email")) {
            authenticate();
        }
    }

    public static void logout() {
        session.clear();
        Application.index();
    }

    public static void error() {
        renderText("Error logging in");
    }

    public static void authenticate() {
        if (OpenID.isAuthenticationResponse()) {
            OpenID.UserInfo verifiedUser = OpenID.getVerifiedID();
            if (verifiedUser == null || !verifiedUser.extensions.containsKey("email") || verifiedUser.extensions.get("email").trim().equals("")) {
                error();
            }
            session.put("email", verifiedUser.extensions.get("email"));
            session.put("name", verifiedUser.extensions.get("firstname") + " " + verifiedUser.extensions.get("lastname"));
            Application.index();
        } else {
            if (!OpenID.id(ZENEXITY_OPENID).
                    required("lastname", "http://axschema.org/namePerson/last").
                    required("firstname", "http://axschema.org/namePerson/first").
                    required("email", "http://axschema.org/contact/email").
                    verify()) {
                error();
            }
        }
    }


}
