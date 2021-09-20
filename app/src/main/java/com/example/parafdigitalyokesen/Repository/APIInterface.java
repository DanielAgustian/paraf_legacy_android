package com.example.parafdigitalyokesen.Repository;



import com.example.parafdigitalyokesen.model.AuthModel;
import com.example.parafdigitalyokesen.model.GetCollabViewModel;
import com.example.parafdigitalyokesen.model.GetConnectModel;
import com.example.parafdigitalyokesen.model.GetDownloadModel;
import com.example.parafdigitalyokesen.model.GetHomeModel;
import com.example.parafdigitalyokesen.model.GetMyInfoModel;
import com.example.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.example.parafdigitalyokesen.model.GetNotifListModel;
import com.example.parafdigitalyokesen.model.GetProfileModel;
import com.example.parafdigitalyokesen.model.GetSaveAllSign;
import com.example.parafdigitalyokesen.model.GetSignDetailModel;
import com.example.parafdigitalyokesen.model.GetSignatureModel;
import com.example.parafdigitalyokesen.model.GetTypeCategoryModel;
import com.example.parafdigitalyokesen.model.LoginModel;
import com.example.parafdigitalyokesen.model.MyInformationModel;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.model.SimpleResponse;
import com.example.parafdigitalyokesen.model.StatHomeModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    //------------------------Register Login API------------------------------------
    @POST("api/v1/register")
    Observable<AuthModel> registerUser(@Body AuthModel auth);

    @FormUrlEncoded
    @POST("api/v1/login")
    Observable<LoginModel> loginUser(@Field("email") String email, @Field("password") String password);


    @POST("api/v1/refresh")
    Observable<LoginModel> refreshToken(@Header("Authorization") String token);

    @POST("api/v1/logout")
    Observable<SimpleResponse> logOutUser(@Header("Authorization") String token);


    //---------------------FORGOT PASSWORD API------------------------------
    @FormUrlEncoded
    @POST("api/v1/forgot-password")
    Observable<SimpleResponse> forgotPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("api/v1/confirm-token")
    Observable<SimpleResponse> checkPasswordToken(@Field("email") String email, @Field("token") String token);

    @FormUrlEncoded
    @POST("api/v1/reset-password")
    Observable<SimpleResponse> resetPassword(@Field("email") String email,
                                             @Field("password") String password,
                                             @Field("password_confirmation") String password_confirmation);

    //--------------------------   Home API -----------------------------------
    @GET("api/v1/home")
    Observable<GetHomeModel> getHomeStat(@Header("Authorization") String token);

    //------------------------------ Profile API---------------
    @GET("api/v1/user")
    Observable<GetProfileModel> getProfile(@Header("Authorization") String token);

    @GET("api/v1/user/my-information")
    Observable<GetMyInfoModel> getMyInfo(@Header("Authorization") String token);

    @PUT("api/v1/user/my-information")
    Observable<SimpleResponse> putMyInfo(@Header("Authorization") String token, @Body MyInformationModel model);

    @FormUrlEncoded
    @PUT("api/v1/user/change-password")
    Observable<SimpleResponse> putChangePassword(@Header("Authorization") String token,
                                           @Field("current_password") String current_password,
                                           @Field("password") String password,
                                           @Field("password_confirmation") String password_confirmation);

    @GET("api/v1/user/my-contact")
    Observable<GetConnectModel> getMyContact(@Header("Authorization") String token);



    //--------------------------- Add new Sign -------------------------------
    @GET("api/v1/categories")
    Observable<GetTypeCategoryModel> getCategories(@Header("Authorization") String token);

    @GET("api/v1/types")
    Observable<GetTypeCategoryModel> getTypes(@Header("Authorization") String token);


    @Multipart
    @POST("api/v1/qr-sign/sign-yourself")
    Observable<GetSignDetailModel> addNewSign(@Header("Authorization") String token,
                                                 @Part("user_name") RequestBody user_name,
                                                 @Part("user_email") RequestBody user_email,
                                                 @Part("name") RequestBody name,
                                                 @Part("category_id") RequestBody category_id,
                                                 @Part("type_id") RequestBody type_id,
                                                 @Part("description") RequestBody description,
                                                 @Part("link") RequestBody link,
                                                 @Part MultipartBody.Part file
                                          );
    @Multipart
    @POST("api/v1/qr-sign/request-signature")
    Observable<GetSignDetailModel> addReqSign(@Header("Authorization") String token,
                                              @Part("user_name") RequestBody user_name,
                                              @Part("user_email") RequestBody user_email,
                                              @Part("name") RequestBody name,
                                              @Part("category_id") RequestBody category_id,
                                              @Part("type_id") RequestBody type_id,
                                              @Part("description") RequestBody description,
                                              @Part("link") RequestBody link,
                                              @Part MultipartBody.Part file,
                                              @Part ("expired_date_at") RequestBody date,
                                              @Part ("expired_time_at") RequestBody time,
                                              @Part ("message") RequestBody message,
                                              @Query("email[]") ArrayList<String> email
    );


    /*---------------------------------- Get Signature List--------------------------------------*/

    @GET("api/v1/sign/my-signature")
    Observable<GetSignatureModel> getMySignList(
            @Header("Authorization") String token
    );

    @GET("api/v1/sign/my-request")
    Observable<GetSignatureModel> getMyReqList(
            @Header("Authorization") String token
    );

    @GET("api/v1/sign/my-request?status=accepted")
    Observable<GetSignatureModel> getMyReqAccList(
            @Header("Authorization") String token
    );
    @GET("api/v1/sign/my-request?status=rejected")
    Observable<GetSignatureModel> getMyReqRejList(
            @Header("Authorization") String token
    );
    //---------------------------------- My Signature details --------------------------------//
    @GET("api/v1/sign/my-signature/{sign_id}")
    Observable<GetSignDetailModel> getMySignDetail(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );





    @FormUrlEncoded
    @PUT("api/v1/document/{sign_id}/rename")
    Observable<SimpleResponse> putRenameSign(
            @Header("Authorization") String token,
            @Path("sign_id") int signId,
            @Field("name") String name
    );

    @DELETE("api/v1/document/{sign_id}/delete")
    Observable<SimpleResponse> putDeleteSign(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );

    @Multipart
    @POST("api/v1/collab/{sign_id}/recreate")
    Observable<GetSignDetailModel> putRecreateSignCollab(
            @Header("Authorization") String token,
            @Path("sign_id") int signId,
            @Part("user_name") RequestBody user_name,
            @Part("user_email") RequestBody user_email,
            @Part("name") RequestBody name,
            @Part("category_id") RequestBody category_id,
            @Part("type_id") RequestBody type_id,
            @Part("description") RequestBody description,
            @Part("link") RequestBody link,
            @Part MultipartBody.Part file,
            @Part("expired_date_at") RequestBody date,
            @Part("expired_time_at") RequestBody time
    );



    @Multipart
    @POST("api/v1/sign/my-signature/{sign_id}/recreate")
    Observable<GetSignDetailModel> putRecreateSign(
            @Header("Authorization") String token,
            @Path("sign_id") int signId,
            @Part("user_name") RequestBody user_name,
            @Part("user_email") RequestBody user_email,
            @Part("name") RequestBody name,
            @Part("category_id") RequestBody category_id,
            @Part("type_id") RequestBody type_id,
            @Part("description") RequestBody description,
            @Part("link") RequestBody link,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("api/v1/document/{sign_id}/invite-signer")
    Observable<SimpleResponse> putInviteSign(
            @Header("Authorization") String token,
            @Path("sign_id") int signId,
            @Field("email[]") ArrayList<String> email,
            @Field("expired_date_at") String date,
            @Field("expired_time_at") String time
    );

    @FormUrlEncoded
    @POST("api/v1/document/{sign_id}/duplicate")
    Observable<SimpleResponse> putDuplicateSign(
            @Header("Authorization") String token,
            @Path("sign_id") int signId,
            @Field("name") String name
    );


    @GET("api/v1/collab/{doc_id}/detail/saveall")
    Observable<GetSaveAllSign> getSAveAllSign(
            @Header("Authorization") String token,
            @Path("doc_id") int docId
    );

    //------------------------ My Request Details ------------------------------//
    @GET("api/v1/sign/my-request/{sign_id}")
    Observable<GetMyReqDetailModel> getMyRequestDetail(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );


    @PUT("api/v1/sign/my-request/{sign_id}/accept")
    Observable<SimpleResponse> putAcceptReq(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );

    @FormUrlEncoded
    @PUT("api/v1/sign/my-request/{sign_id}/decline")
    Observable<SimpleResponse> putRejectedReq(
            @Header("Authorization") String token,
            @Path("sign_id") int signId,
            @Field("feedback") String feedback
    );


    @GET("api/v1/document/{sign_id}/remind")
    Observable<SimpleResponse> GetRemindReq(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );


    //--------------------------------Get Collab List------------------------------------//
    @GET("api/v1/collab/requested")
    Observable<GetSignatureModel> getCollabReqList(
            @Header("Authorization") String token
    );

    @GET("api/v1/collab/rejected")
    Observable<GetSignatureModel> getCollabRejectedList(
            @Header("Authorization") String token
    );

    @GET("api/v1/collab/accepted")
    Observable<GetSignatureModel> getCollabAcceptedList(
            @Header("Authorization") String token
    );

    //--------------------------Collab Detail--------------------------//
    @GET("api/v1/collab/{sign_id}/detail")
    Observable<GetMyReqDetailModel> getCollabDetail(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );

    @GET("api/v1/collab/{sign_id}/detail/{collab_id}/view")
    Observable<GetCollabViewModel> getCollabDetailView(
            @Header("Authorization") String token,
            @Path("collab_id") int collab_id,
            @Path("sign_id") int sign_id
    );
    //-------------------------- Send document and request document--------------------------//
    @Multipart
    @POST("api/v1/collab/{id}/send-document")
    Observable<SimpleResponse> SendDocument(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Query("email[]") ArrayList<String> email,
            @Part MultipartBody.Part file,

            @Part("message") RequestBody message
    );
    @POST("api/v1/sign/my-request/{sign_id}/request-document")
    Observable<SimpleResponse> RequestDocument(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );

    @GET("api/v1/sign/my-request/{sign_id}/download")
    Observable<GetDownloadModel> DownloadFinalDocument(
            @Header("Authorization") String token,
            @Path("sign_id") int signId
    );


    //-------------------------- SEARCHING DATA--------------------------------//
    @GET("api/v1/search")
    Observable<GetSignatureModel> GetSearchData(
            @Header("Authorization") String token
    );

    @GET("api/v1/search/waiting-signature")
    Observable<GetSignatureModel> GetSearchWaitingData(
            @Header("Authorization") String token
    );
    @GET("api/v1/search/accepted-signature")
    Observable<GetSignatureModel> GetSearchAcceptedData(
            @Header("Authorization") String token
    );
    @GET("api/v1/search/rejected-signature")
    Observable<GetSignatureModel> GetSearchRejectedData(
            @Header("Authorization") String token
    );

    //----------------------- NOTIFICATION LIST --------------------------------------//
    @GET("api/v1/notifications")
    Observable<GetNotifListModel> GetNotificationList(
            @Header("Authorization") String token
    );

    @GET("api/v1/notifications/mark-read")
    Observable<SimpleResponse> ReadNotifAll(
            @Header("Authorization") String token
    );

    @GET("api/v1/notifications/{id_notif}")
    Observable<SimpleResponse> ReadNotifOnce(
            @Header("Authorization") String token,
            @Path("id_notif") int id
    );



}
