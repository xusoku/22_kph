package com.davis.kangpinhui.api;

import com.davis.kangpinhui.util.LogUtils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by davis on 16/5/17.
 */
public class ApiInstant {


    private static ApiService service = null;


    public static final ApiService getInstant() {

        if (service == null) {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();


//            OkHttpClient httpClient = new OkHttpClient();
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();

            OkHttpClient httpClient =new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response response = chain.proceed(chain.request());

                            LogUtils.e("url",response.request().url().toString());
                            return response;
                        }
                    })
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
//                    .client(httpClient)
//                    .baseUrl(ApiService.baseurl)
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .build();

                    .baseUrl(ApiService.baseurl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();




            service = retrofit.create(ApiService.class);

        }
        return service;
    }

    ;


}
