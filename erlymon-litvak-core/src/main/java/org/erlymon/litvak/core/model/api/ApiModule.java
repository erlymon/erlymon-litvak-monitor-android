/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of TraccarLitvakM (fork Erlymon Monitor).
 *
 * TraccarLitvakM (fork Erlymon Monitor) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraccarLitvakM (fork Erlymon Monitor) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraccarLitvakM (fork Erlymon Monitor).  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.litvak.core.model.api;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.erlymon.litvak.core.model.data.User;
import org.erlymon.litvak.core.model.data.UserSerializer;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/18/16.
 */
public class ApiModule {
    private static final int CONNECT_TIMEOUT = 5;
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 5;

    private static ApiModule ourInstance = new ApiModule();

    private Gson gson;
    private ApiInterface apiInterface;


    public synchronized static ApiModule getInstance() {
        return ourInstance;
    }

    private ApiModule() {}

    public void init(Context context, String dns, boolean sslOrTls) {
        // init gson
        gson = new GsonBuilder()
                .setDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
                .setPrettyPrinting()
                .registerTypeAdapter(User.class, new UserSerializer())
                .create();


        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MINUTES)
                .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                //.addNetworkInterceptor(new StethoInterceptor())
                .cookieJar(cookieJar)
                .build();


        apiInterface = new Retrofit.Builder()
                .baseUrl("{protocol}://{dns}/traccar/rest/".replace("{protocol}", sslOrTls ? "https" : "http").replace("{dns}", dns))
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiInterface.class);
    }

    public ApiInterface getApi() {
        return apiInterface;
    }

    public Gson getGson() {
        return gson;
    }
}
