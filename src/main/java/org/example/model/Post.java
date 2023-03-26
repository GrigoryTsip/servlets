package org.example.model;

import java.util.concurrent.atomic.AtomicLong;

public class Post {
    private long id;
    private String content;
    private static final AtomicLong idCount = new AtomicLong();
    
    public Post() {
    }
    
    public Post(String content) {
        id = idCount.incrementAndGet();
        this.content = content;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
