package org.example.servlet;

import org.example.controller.PostController;
import org.example.model.Post;
import org.example.repository.PostRepository;
import org.example.service.PostService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.Annotation;
import java.text.ParseException;
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
        // В конфигурации к applicationContext надо указывать путь к "org.example" там где у вас лежат
        // другие анатации (@Controller, @Repository, @Service)
        // Spring при запуске просматривает папки внутри того пути который вы указали, на наличие анотаций
//        final var context = new AnnotationConfigApplicationContext("org.example.servlet");
        final var context = new AnnotationConfigApplicationContext("org.example");
        
        // Вам не надо создавать еще один контролер! Он у вас уже объявлен в поле класса
        // если вы создатите его здесь, то не сможете использовать его в методе service()
//        final var controller = context.getBean("postController", PostController.class);
        controller = context.getBean("postController", PostController.class);
        
        // суть Spring'а как раз в том что зависимости остальные он сделает сам
        // просконироует все папки найдет анотации которые нужны для создания PostController'а
        // Создание service на лекции было для того, чтобы показать что можно возвращать bean не
        // только по нозванию, но и по классу
//        final var service = context.getBean(PostService.class);
        
        standRequest.put(GET, ServiceEnum.getServEnum(GET));
        standRequest.put(POST, ServiceEnum.getServEnum(POST));
        standRequest.put(DELETE, ServiceEnum.getServEnum(DELETE));
        
        threadPool = Executors.newFixedThreadPool(6);
        
        // for test -------------------------------------------------------
//        try {
//            service.save(new Post("Первый пост"));
//            service.save(new Post("Второй пост"));
//            service.save(new Post("Третий пост"));
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        //-----------------------------------------------------------------
    }
    /*
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
     */
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
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
        while (!task.isDone()) continue;
    }
    
}