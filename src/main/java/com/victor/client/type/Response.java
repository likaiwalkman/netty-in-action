package com.victor.client.type;

public class Response {
    public Response(){

    }

    public Response(String content) {
        this.content = content;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
