package org.example.servlet;

import org.example.controller.PostController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.example.servlet.MethEnum.*;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private Map<String, ServiceEnum> servEnum;
    public static Map<MethEnum, HashMap<String, ServiceEnum>> standRequest = new HashMap<>();
    public static ExecutorService threadPool;
    
    @Override
    public void init() {
        
        final var context = new AnnotationConfigApplicationContext("org.example");
        controller = context.getBean("postController", PostController.class);
        
        standRequest.put(GET, ServiceEnum.getServEnum(GET));
        standRequest.put(POST, ServiceEnum.getServEnum(POST));
        standRequest.put(DELETE, ServiceEnum.getServEnum(DELETE));
        
        threadPool = Executors.newFixedThreadPool(6);
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        
        Runnable methThread = () -> {
            try {
                final var path = req.getRequestURI();
                final var method = MethEnum.valueOf(req.getMethod());
                
                long id = 0;
                String standPath = path;
                String subs = path.substring(path.lastIndexOf("/") + 1);
                if (subs.matches("[-+]?\\d+")) {
                    id = Long.parseLong(subs);
                    standPath = path.substring(0, path.lastIndexOf("/") + 1) + "\\d+";
                }
                
                if (standRequest.containsKey(method)) {
                    servEnum = standRequest.get(method);
                    if (servEnum.containsKey(standPath)) {
                        ServiceEnum serv = servEnum.get(standPath);
                        
                        switch (serv) {
                            case ALL -> controller.all(resp);
                            case GET_BY_ID -> controller.getById(id, resp);
                            case SAVE -> controller.save(req.getReader(), resp);
                            case REMOVE -> controller.removeById(id, resp);
                        }
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        };
        
        
        Future<?> task = threadPool.submit(methThread);
        while (!task.isDone()) {  }
    }
    
}