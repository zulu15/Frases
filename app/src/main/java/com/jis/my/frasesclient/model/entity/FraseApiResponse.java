package com.jis.my.frasesclient.model.entity;

import java.util.List;

public class FraseApiResponse {
    private List<Frase> frases;

    public List<Frase> getFrases() {
        return frases;
    }

    public void setFrases(List<Frase> frases) {
        this.frases = frases;
    }

    public FraseApiResponse(List<Frase> frases) {
        this.frases = frases;
    }

    public FraseApiResponse() {
    }
}
