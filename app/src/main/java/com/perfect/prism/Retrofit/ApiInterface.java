package com.perfect.prism.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST("Report/GetTicketCountSummaryDetails")
    Call<String> getTicketCount(@Body RequestBody body);

    @POST("agent/AgentProductDetails")
    Call<String> getProducts(@Body RequestBody body);

    @POST("Report/GetAllAgentDetails")
    Call<String> getAgent(@Body RequestBody body);

    @POST("Report/GetAllClientDetails")
    Call<String> getClient(@Body RequestBody body);

    @POST("agent/TicketsCountWiseClientDets")
    Call<String> getTicketsCountWiseClient(@Body RequestBody body);

    @POST("agent/AgentLogin")
    Call<String>getLogin(@Body RequestBody body);

    @POST("agent/VerificationCall")
    Call<String>getOtp(@Body RequestBody body);

    @POST("Report/ClientWiseTicketAgeingDetails")
    Call<String>geAgeingReport(@Body RequestBody body);

    @POST("report/AgentLocationHourDetails")
    Call<String>getLocationReport(@Body RequestBody body);

    @POST("Report/AgeingCountWiseProductsDetails")
    Call<String>getAgeingCountReport(@Body RequestBody body);

    @POST("Report/GetAgentWiseTicketCountDetails")
    Call<String>getAgentwiseTicketCount(@Body RequestBody body);

    @POST("agent/AgentTicketNotification")
    Call<String>getHomeNotificationcount(@Body RequestBody body);

    @POST("agent/TicketsCountWiseProductDets")
    Call<String>getProductDet(@Body RequestBody body);

    @POST("Report/GetClientWiseTicketCountDetails")
    Call<String>getClientwiseTicketCount(@Body RequestBody body);

    @POST("agent/AgentTicketsSelect")
    Call<String>getAgentTicket(@Body  RequestBody body);

    @POST("agent/AgentTicketIndividualsSelect")
    Call<String>getAgentTicketDetails(@Body  RequestBody body);


    @POST("agent/DepartmentandTeamDropdownBind")
    Call<String>getDepartmentAndTeam(@Body  RequestBody body);

    @POST("agent/AgentDropdownBind")
    Call<String>getAgentList(@Body  RequestBody body);

    @POST("agent/UpdateTicketAssign")
    Call<String>assignTicket(@Body  RequestBody body);

    @POST("agent/UploadAction")
    Call<String>addRemarkWithoutImage(@Body  RequestBody body);

    @Multipart
    @POST("agent/UploadActionWithImage")
    Call<String> addRemark(@Part("JsonData") RequestBody JsonData,
                               @Part MultipartBody.Part ImgFile);


    @POST("Report/AgentLocationHourDetails")
    Call<String>agentLocationHourDetails(@Body  RequestBody body);

   /* @Multipart
    @POST("agent/UploadAction")
    Call<String> addRemark(@Part("ResponseType") RequestBody ResponseType,
                           @Part("LoginMode") RequestBody LoginMode,
                           @Part("Token") RequestBody Token,
                           @Part("ID_Tickets") RequestBody ID_Tickets,
                           @Part("Agent_ID") RequestBody Agent_ID,
                           @Part("AgentTo") RequestBody AgentTo,
                           @Part("FK_BugType") RequestBody FK_BugType,
                           @Part("TransStatus") RequestBody TransStatus,
                           @Part("Description") RequestBody Description,
                           @Part("AgentNotes") RequestBody AgentNotes,
                           @Part("FK_Company") RequestBody FK_Company,
                           @Part("HourTaken") RequestBody HourTaken,
                           @Part("Location_Latitude") RequestBody Location_Latitude,
                           @Part("Location_Longitude") RequestBody Location_Longitude,
                           @Part("Location_Name") RequestBody Location_Name,
                           @Part MultipartBody.Part XmlAttachment);*/


    @POST( "agent/AgentTicketTimeStatusUpdate" )
    Call<String>getAgentTicketTimeStatus(@Body RequestBody body);

    @POST("agent/DepartmentProductDetails")
    Call<String> getDepProducts(@Body RequestBody body);

    @POST("agent/ClientDetails")
    Call<String> getClientDetails(@Body RequestBody body);

    @POST("agent/AgentProductDetails")
    Call<String> getAgentProductDetails(@Body RequestBody body);

    @POST("agent/TicketTopicDetails")
    Call<String> getTicketTopicDetails(@Body RequestBody body);

    @POST("agent/TicketNumberGenerate")
    Call<String> getTicketNumberGenerate(@Body RequestBody body);

//    agent/AgentTicketCreation
    @Multipart
    @POST("agent/AgentTicketCreationWithAttachement")
    Call<String> getAgentTicketCreation(@Part("JsonData") RequestBody JsonData,
                                        @Part MultipartBody.Part ImgFile);

    @POST("agent/AgentTicketCreation")
    Call<String> getAgentTicketCreationWithoutImg(@Body RequestBody JsonData);

    @POST("agent/AgentLoginLocationDet")
    Call<String> getAgentLoginLocationDet(@Body RequestBody JsonData);

    @POST("Report/RptAgentLocationDetails")
    Call<String> getRptAgentLocationDetails(@Body RequestBody body);


}