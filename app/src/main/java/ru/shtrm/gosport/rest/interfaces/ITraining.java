package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Training;

public interface ITraining {
    @GET("/api/trainings")
    Call<List<Training>> training();

    @GET("/api/trainings")
    Call<List<Training>> training(@Query("changedAfter") String changedAfter);

    @GET("/api/trainings")
    Call<List<Training>> trainingById(@Query("id") String id);

    @POST("/api/trainings")
    Call<ResponseBody> sendTraining(@Body List<Training> training);
}
