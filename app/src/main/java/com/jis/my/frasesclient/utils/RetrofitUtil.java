package com.jis.my.frasesclient.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static final String BASE_URL = "http://ec2-3-88-169-107.compute-1.amazonaws.com:8001/";
    private static Retrofit retrofit;

    public static Retrofit GetRetrofitInstance(){
        if(retrofit == null){
             retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
