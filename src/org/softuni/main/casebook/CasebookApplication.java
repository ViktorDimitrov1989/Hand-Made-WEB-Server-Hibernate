package org.softuni.main.casebook;


import javafx.stage.Stage;
import org.softuni.main.casebook.handlers.HomeHandler;
import org.softuni.main.javache.Application;
import org.softuni.main.javache.http.HttpContext;
import org.softuni.main.javache.http.HttpResponse;
import org.softuni.main.javache.http.HttpSession;
import org.softuni.main.javache.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CasebookApplication implements Application {

    private HttpSession session;

    private HashMap<String, Function<HttpContext, byte[]>> routeTable;

    public CasebookApplication(){
        this.initializeRoutes();
    }

    private void initializeRoutes(){
        this.routeTable = new HashMap<>();

        this.routeTable.put("/", (HttpContext x) ->
                new HomeHandler().
                        index(x.getHttpRequest(), x.getHttpResponse()).getBytes());
    }

    @Override
    public byte[] handleRequest(HttpContext httpContext) {
        String requestUrl = httpContext.getHttpRequest().getRequestUrl();

        if(!this.getRoutes().containsKey(requestUrl)){
            //NOT FOUND
            return this.notFound(httpContext).getBytes();
        }

        return this.getRoutes().get(requestUrl).apply(httpContext);
    }

    private HttpResponse notFound(HttpContext httpContext) {
        HttpResponse httpResponse = httpContext.getHttpResponse();

        httpResponse.setStatusCode(HttpStatus.NOT_FOUND);
        httpResponse.addHeader("Content-Type", "text/html");
        httpResponse.setContent("<h1>Error (404): The resource you are looking for is incalid</h1>".getBytes());

        return httpResponse;
    }

    @Override
    public Map<String, Function<HttpContext, byte[]>> getRoutes() {
        return Collections.unmodifiableMap(this.routeTable);
    }

    @Override
    public HttpSession getSession() {
        return this.session;
    }

    @Override
    public void setSession(HttpSession session) {
        this.session = session;
    }
}
