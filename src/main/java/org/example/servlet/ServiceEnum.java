package org.example.servlet;

import java.util.HashMap;

public enum ServiceEnum {
    
    ALL,
    GET_BY_ID,
    SAVE,
    REMOVE;
    
    public static HashMap<String, ServiceEnum> getServEnum(MethEnum meth) {
        HashMap<String, ServiceEnum> serv = new HashMap<>();
        switch (meth) {
            case GET -> {
                serv.put("/api/posts", ALL);
                serv.put("/api/posts/\\d+", GET_BY_ID);
            }
            case POST -> serv.put("/api/posts", SAVE);
            case DELETE -> serv.put("/api/posts/\\d+", REMOVE);
        }
        return serv;
    }
}
