package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Stadium;

public interface IStadium {
    @GET("/api/stadiums")
    Call<List<Stadium>> stadium();

    @GET("/api/stadiums")
    Call<List<Stadium>> stadium(@Query("changedAfter") String changedAfter);

    @GET("/api/stadiums")
    Call<List<Stadium>> stadiumById(@Query("id") String id);
}
