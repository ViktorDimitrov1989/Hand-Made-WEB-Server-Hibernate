package org.softuni.main.casebook;


import org.softuni.main.casebook.handlers.fixed.ErrorHandler;
import org.softuni.main.casebook.handlers.fixed.ResourceHandler;
import org.softuni.main.casebook.utils.HandlerLoader;
import org.softuni.main.javache.Application;
import org.softuni.main.javache.http.HttpContext;
import org.softuni.main.javache.http.HttpResponse;
import org.softuni.main.javache.http.HttpSession;
import org.softuni.main.javache.http.HttpStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CasebookApplication implements Application {

    private HttpSession session;

    private HashMap<String, Function<HttpContext, byte[]>> routesTable;

    private final ErrorHandler errorHandler = new ErrorHandler();

    private final ResourceHandler resourceHandler = new ResourceHandler();


    public CasebookApplication(){
        this.initializeRoutes();
    }


    private Map<String, Method> loadActionsForMethod(String method, HandlerLoader handlerLoader){
        Map<String, Method> actions = handlerLoader.retrieveActionsMap(method);

        for (Map.Entry<String, Method> actionEntry : actions.entrySet()) {

            try {
                Object handlerObject = actionEntry.getValue()
                        .getDeclaringClass()
                        .getConstructor()
                        .newInstance();

                this.routesTable.put(actionEntry.getKey(),
                        (HttpContext httpContext) -> {
                            if(!httpContext.getHttpRequest().getMethod().equals(method)){
                                return this.errorHandler.badRequest(httpContext.getHttpRequest(), httpContext.getHttpResponse())
                                        .getBytes();
                            }

                            try {
                                return ((HttpResponse)actionEntry.getValue()
                                        .invoke(handlerObject,
                                                httpContext.getHttpRequest(),
                                                httpContext.getHttpResponse())).getBytes();

                            } catch (IllegalAccessException
                                    | InvocationTargetException e) {
                                e.printStackTrace();
                            }

                            return null;
                        });

            } catch (InstantiationException
                    | NoSuchMethodException
                    | InvocationTargetException
                    | IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        return actions;
    }

    private void initializeRoutes(){
        this.routesTable = new HashMap<>();

        HandlerLoader handlerLoader = new HandlerLoader();

        Map<String, Method> postActions = this.loadActionsForMethod("POST", handlerLoader);
        Map<String, Method> getActions = this.loadActionsForMethod("GET", handlerLoader);

    }

    @Override
    public byte[] handleRequest(HttpContext httpContext) {
        String requestUrl = httpContext.getHttpRequest().getRequestUrl();

        if(!this.getRoutes().containsKey(requestUrl)){
            return this.errorHandler.notFound(httpContext.getHttpRequest(),
                    httpContext.getHttpResponse())
                    .getBytes();
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
        return Collections.unmodifiableMap(this.routesTable);
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
