package ru.shtrm.gosport.rest.interfaces;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface IFileDownload {
    @GET
    Call<ResponseBody> getFile(@Url String url);

    @Multipart
    @POST("/api/trainings/upload")
    Call<ResponseBody> uploadFiles(@Part("descr") RequestBody descr, @Part List<MultipartBody.Part> files);
}
