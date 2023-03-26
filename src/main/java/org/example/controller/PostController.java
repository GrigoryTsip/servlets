package org.example.controller;

import com.google.gson.Gson;
import org.example.model.Post;
import org.example.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;
    
    public PostController(PostService service) {
        this.service = service;
    }
    
    public void all(HttpServletResponse response) throws IOException {
        final var data = service.all();
        writeResponce(data, response);
    }
    
    public void getById(long id, HttpServletResponse response) throws IOException {
        List<Post> data = new ArrayList<>();
        data.add(service.getById(id));
        writeResponce(data, response);
     }
    
    public void save(Reader body, HttpServletResponse response) throws IOException, ExecutionException, InterruptedException {
        final var post = new Gson().fromJson(body, Post.class);
        ArrayList<Post> data = new ArrayList<>();
        data.add(service.save(post));
        writeResponce(data, response);
    }
    
    public void removeById(long id, HttpServletResponse response) throws IOException {
        service.removeById(id);
        this.all(response);
    }
    
    private void writeResponce(List<Post> data, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final var gson = new Gson();
        response.getWriter().print(gson.toJson(data));
    }
}
