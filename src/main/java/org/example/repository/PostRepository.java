package org.example.repository;

import org.example.exception.NotFoundException;
import org.example.model.Post;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.example.servlet.MainServlet.threadPool;

// Stub
public class PostRepository {
    
    private static final ConcurrentHashMap<Long, Post> postMap = new ConcurrentHashMap<>();
    
    public List<Post> all() {
        if (postMap.isEmpty()) {
            throw new NotFoundException("Список постов пуст");
        }
        List<Post> data = new ArrayList<>();
        for (Map.Entry<Long, Post> entry : postMap.entrySet()) {
            data.add(entry.getValue());
        }
         return data;
    }
    
    public Optional<Post> getById(long id) {
        if (postMap.containsKey(id)) {
            return Optional.ofNullable(postMap.get(id));
        }
        throw new NotFoundException("Неверный идентификатор поста");
    }
    
    public Post save(Post post) throws ExecutionException, InterruptedException {
             if (postMap.containsKey(post.getId())) {
                postMap.put(post.getId(), post);
            } else {
                Post newPost = new Post(post.getContent());
                postMap.put(newPost.getId(), newPost);
                return newPost;
            }
            return post;
        }
 
    public void removeById(long id) {
        postMap.remove(id);
    }
}
