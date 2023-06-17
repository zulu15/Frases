package com.jis.my.frasesclient.model.entity;

import java.io.Serializable;

public class Frase implements Serializable {
    private Long id;
    private String frase;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
    }

    @Override
    public String toString() {
        return this.frase;
    }

    public Frase(Long id, String frase) {
        this.id = id;
        this.frase = frase;
    }

    public Frase() {
    }
}
