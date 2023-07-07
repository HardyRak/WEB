package etu1986.framework.servlet;

import etu1986.framework.Mapping;
import etu1986.framework.MethodAnnotation;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FrontServlet extends HttpServlet {
    RequestDispatcher dispat;

    public RequestDispatcher getDispat() {
        return this.dispat;
    }
    public void setDispat(RequestDispatcher dispat) {
        this.dispat = dispat;
    }
    private HashMap<String, Mapping> MappingUrls;
    public HashMap<String, Mapping> getMappingUrls() {
        return MappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        MappingUrls = mappingUrls;
    }
    @Override
    public void init() throws ServletException {
        try {
            this.fillMappingUrls();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
    
    public List<File> packagesFiles(String[] packages){
        List<File> files = new ArrayList<>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for(String pkg : packages){
            files.add(new File(Objects.requireNonNull(classLoader.getResource(pkg.replace('.', '/'))).getFile()));
        }
        return files;
    }
    
    private void fillMappingUrls() throws ClassNotFoundException {
        this.setMappingUrls(new HashMap<String, Mapping>());

        String[] packages = new String[1];
        packages[0] = "etu1986.framework.servlet";
        List<File> packageDir = this.packagesFiles(packages);

        for(int i=0 ; i < packages.length ; i++){
            if(packageDir.get(i).exists()){
                String[] files = packageDir.get(i).list();
                assert files != null;
                for(String file : files){
                    if(file.endsWith(".class")){
                        String className = file.substring(0, file.length()-6 /* exemple: test.class >> enlever .class */);
                        Class<?> clazz = Class.forName(packages[i] + '.' + className);
                        for(Method method : clazz.getDeclaredMethods()){
                            if(method.isAnnotationPresent(MethodAnnotation.class)){
                                MethodAnnotation methannot = (MethodAnnotation) method.getAnnotation(MethodAnnotation.class);
                                this.getMappingUrls().put(methannot.name(), new Mapping(className, method.getName()));
                                System.out.println("Class: "+className);
                                System.out.println("Method: "+methannot.name()+" \n");
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Mapping length: "+this.getMappingUrls().size());
    }
}
