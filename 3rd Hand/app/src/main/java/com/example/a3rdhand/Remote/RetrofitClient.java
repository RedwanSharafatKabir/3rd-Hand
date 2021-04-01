package com.example.a3rdhand.Remote;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;
    private static final String BaseUrl = "https://maps.googleapis.com/";

    public static Retrofit getInstance() {
        return instance==null ? new Retrofit.Builder().baseUrl(BaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build() : instance;
    }
}
