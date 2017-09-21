package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.UserTraining;

public interface IUserTraining {
    @GET("/api/objects/user-training")
    Call<List<UserTraining>> userTraining();

    @GET("/api/objects/user-training")
    Call<List<UserTraining>> userTraining(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/user-training")
    Call<List<UserTraining>> userTrainingById(@Query("id") String id);

    @POST("/api/objects/user-training")
    Call<ResponseBody> sendUserTraining(@Body List<UserTraining> userTraining);

}
