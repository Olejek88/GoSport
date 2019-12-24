package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.shtrm.gosport.db.realm.Event;

public interface IEvent {
    @GET("/api/events")
    Call<List<Event>> events();

    @GET("/api/events")
    Call<List<Event>> events(@Query("changedAfter") String changedAfter);

    @GET("/api/events")
    Call<List<Event>> eventsById(@Query("id") String id);
}
