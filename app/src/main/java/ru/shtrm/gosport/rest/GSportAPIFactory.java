package ru.shtrm.gosport.rest;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.shtrm.gosport.BuildConfig;
import ru.shtrm.gosport.GoSportApplication;
import ru.shtrm.gosport.deserializer.DateTypeDeserializer;
import ru.shtrm.gosport.model.AuthorizedUser;
import ru.shtrm.gosport.rest.interfaces.IAmplua;
import ru.shtrm.gosport.rest.interfaces.IEvent;
import ru.shtrm.gosport.rest.interfaces.IFileDownload;
import ru.shtrm.gosport.rest.interfaces.ILevel;
import ru.shtrm.gosport.rest.interfaces.ISport;
import ru.shtrm.gosport.rest.interfaces.IStadium;
import ru.shtrm.gosport.rest.interfaces.ITeam;
import ru.shtrm.gosport.rest.interfaces.ITokenService;
import ru.shtrm.gosport.rest.interfaces.ITraining;
import ru.shtrm.gosport.rest.interfaces.IUserService;
import ru.shtrm.gosport.rest.interfaces.IUserSport;
import ru.shtrm.gosport.rest.interfaces.IUserTraining;

public class GSportAPIFactory {
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    private static final OkHttpClient CLIENT = new OkHttpClient()
            .newBuilder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request origRequest = chain.request();
                    Headers origHeaders = origRequest.headers();
                    AuthorizedUser user = AuthorizedUser.getInstance();
                    Headers newHeaders = origHeaders.newBuilder().add("Authorization", user.getBearer()).build();
                    Request.Builder requestBuilder = origRequest.newBuilder().headers(newHeaders);
                    Request newRequest = requestBuilder.build();
                    return chain.proceed(newRequest);
                }
            })
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    if (BuildConfig.DEBUG) {
                        Request request = chain.request();
                        HttpUrl url = request.url()
                                .newBuilder()
                                .addQueryParameter("XDEBUG_SESSION_START", "netbeans-xdebug")
                                .build();
                        Request.Builder requestBuilder = request.newBuilder().url(url);
                        Request newRequest = requestBuilder.build();
                        return chain.proceed(newRequest);
                    } else {
                        return chain.proceed(chain.request());
                    }
                }
            })
            .build();

    @NonNull
    public static ITokenService getTokenService() {
        return getRetrofit().create(ITokenService.class);
    }

    @NonNull
    public static IUserService getUserService() {
        return getRetrofit().create(IUserService.class);
    }

    @NonNull
    public static IAmplua getAmpluaService() {
        return getRetrofit().create(IAmplua.class);
    }

    @NonNull
    public static ILevel getLevelService() {
        return getRetrofit().create(ILevel.class);
    }

    @NonNull
    public static IEvent getEventService() {
        return getRetrofit().create(IEvent.class);
    }

    @NonNull
    public static ITeam getTeamService() {
        return getRetrofit().create(ITeam.class);
    }

    @NonNull
    public static ISport getSportService() {
        return getRetrofit().create(ISport.class);
    }

    @NonNull
    public static IStadium getStadiumService() {
        return getRetrofit().create(IStadium.class);
    }

    @NonNull
    public static ITraining getTrainingService() {
        return getRetrofit().create(ITraining.class);
    }

    @NonNull
    public static IUserSport getUserSportService() {
        return getRetrofit().create(IUserSport.class);
    }

    @NonNull
    public static IUserTraining getUserTrainingService() {
        return getRetrofit().create(IUserTraining.class);
    }

    @NonNull
    public static IFileDownload getFileDownload() {
        return getRetrofit().create(IFileDownload.class);
    }

    @NonNull
    private static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeDeserializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
        return new Retrofit.Builder()
                .baseUrl(GoSportApplication.serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(CLIENT)
                .build();
    }
}
