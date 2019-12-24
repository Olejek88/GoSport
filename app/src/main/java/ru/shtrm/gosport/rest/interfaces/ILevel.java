package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Level;

public interface ILevel {

    @GET("/api/levels")
    Call<List<Level>> level();

    @GET("/api/levels")
    Call<List<Level>> level(@Query("changedAfter") String changedAfter);

    @GET("/api/levels")
    Call<List<Level>> levelById(@Query("id") String id);
}
