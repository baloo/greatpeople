package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class)
public class Application extends Controller {

    public static void index() {
        render();
    }

}