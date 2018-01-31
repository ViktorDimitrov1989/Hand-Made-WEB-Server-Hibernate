package org.softuni.main.javache;

import org.softuni.main.javache.http.HttpContext;
import org.softuni.main.javache.http.HttpSession;

import java.util.Map;
import java.util.function.Function;

public interface Application {

    byte[] handleRequest(HttpContext httpContext);

    Map<String, Map<String, Function<HttpContext, byte[]>>> getRoutes();

    HttpSession getSession();

    void setSession(HttpSession session);

}
