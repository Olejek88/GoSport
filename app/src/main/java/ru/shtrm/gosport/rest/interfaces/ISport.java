package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Sport;

public interface ISport {
    @GET("/api/sports")
    Call<List<Sport>> sport();

    @GET("/api/sports")
    Call<List<Sport>> sport(@Query("changedAfter") String changedAfter);

    @GET("/api/sports")
    Call<List<Sport>> sportById(@Query("id") String id);
}
