package etu1986.framework.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class FrontServlet extends HttpServlet {
    RequestDispatcher dispat;

    public RequestDispatcher getDispat() {
        return this.dispat;
    }
    public void setDispat(RequestDispatcher dispat) {
        this.dispat = dispat;
    }
    
    public void redirect(String indexOfFile,HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.setDispat(req.getRequestDispatcher(indexOfFile));
        this.getDispat().forward(req,resp);
    }
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String incommingURL = String.valueOf(req.getRequestURL());
        String target = this.getTarget(incommingURL);
        System.out.println("URL target >> "+target);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.processRequest(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        this.processRequest(req,resp);
    }

    private String removeHttpProtocoleStr(String URL){
        return URL.split("//")[1];
    }

    private String getTarget(String URL){
        URL = removeHttpProtocoleStr(URL);

        if(URL.toLowerCase().contains("_war")){
            System.out.println("'war' artifact detected");
            return URL.split("/")[2];
        }else{
            return URL.split("/")[1];
        }
    }
}
