package org.softuni.main.casebook.handlers.dynamic;

import org.softuni.main.casebook.annotations.ApplicationRequestHandler;
import org.softuni.main.casebook.annotations.Get;
import org.softuni.main.javache.http.HttpRequest;
import org.softuni.main.javache.http.HttpResponse;
import org.softuni.main.javache.http.HttpStatus;

@ApplicationRequestHandler
public class HomeHandler {

    public HomeHandler(){
    }

    @Get(route = "/")
    public HttpResponse index(HttpRequest request, HttpResponse response){
        response.setStatusCode(HttpStatus.OK);

        response.addHeader("Content-Type", "text/html");
        response.setContent("<h1>Hello From Casebook</h1><hr/><h2>This is Home</h2>".getBytes());

        return response;
    }

    @Get(route = "/login")
    public HttpResponse login(HttpRequest request, HttpResponse response){
        response.setStatusCode(HttpStatus.OK);

        response.addHeader("Content-Type", "text/html");
        response.setContent(("<h1>Login Page</h1><hr/><h2>This is Login</h2>" +
                "<form method=\"POST\">" +
                "<button type=\"submit\">Submit</button></form>").getBytes());

        return response;
    }


}
