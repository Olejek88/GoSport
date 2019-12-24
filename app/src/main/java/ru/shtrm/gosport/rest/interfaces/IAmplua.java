package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Amplua;


public interface IAmplua {
    @GET("/api/ampluas")
    Call<List<Amplua>> amplua();

    @GET("/api/ampluas")
    Call<List<Amplua>> amplua(@Query("changedAfter") String changedAfter);

    @GET("/api/ampluas")
    Call<List<Amplua>> ampluaById(@Query("id") String id);
}
