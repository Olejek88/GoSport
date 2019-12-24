package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.User;

public interface IUserService {
    @GET("/api/account/me")
    Call<User> user();

    @GET("/api/users")
    Call<List<User>> user(@Query("changedAfter") String changedAfter);

    @POST("/api/user")
    Call<ResponseBody> sendUser(@Body User user);

}
