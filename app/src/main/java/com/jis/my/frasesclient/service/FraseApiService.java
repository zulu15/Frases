package com.jis.my.frasesclient.service;

import com.jis.my.frasesclient.model.entity.Frase;
import com.jis.my.frasesclient.model.entity.FraseApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FraseApiService {

    @GET("api/frases/")
    Call<FraseApiResponse> getFrases();

    @POST("api/frases")
    Call<Frase> saveFrase(@Body Frase frase);

    @DELETE("api/frases/{id}")
    Call<Void> deleteItem(@Path("id") Long itemId);
}
