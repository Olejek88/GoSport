package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Team;

public interface ITeam {
    @GET("/api/teams")
    Call<List<Team>> team();

    @GET("/api/teams")
    Call<List<Team>> team(@Query("changedAfter") String changedAfter);

    @GET("/api/teams")
    Call<List<Team>> teamById(@Query("id") String id);
}
