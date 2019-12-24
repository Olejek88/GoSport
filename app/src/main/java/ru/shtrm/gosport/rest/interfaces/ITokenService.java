package ru.shtrm.gosport.rest.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.shtrm.gosport.serverapi.TokenSrv;

public interface ITokenService {
    @FormUrlEncoded
    @POST("/token")
    Call<TokenSrv> tokenByUUID(@Field("label") String uuid, @Field("grant_type") String grantType);

    @FormUrlEncoded
    @POST("/token")
    Call<TokenSrv> tokenByPassword(@Field("password") String tagId, @Field("grant_type") String grantType);
}
