package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.UserSport;

public interface IUserSport {
    @GET("/api/objects/user-sport")
    Call<List<UserSport>> userSport();

    @GET("/api/objects/user-sport")
    Call<List<UserSport>> userSport(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/user-sport")
    Call<List<UserSport>> userSportById(@Query("id") String id);

    @POST("/api/objects/user-sport")
    Call<ResponseBody> sendUserSport(@Body List<UserSport> userSport);

}
