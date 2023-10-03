package ru.soft.feature_home.data

import retrofit2.*
import retrofit2.http.*
import ru.soft.feature_home.data.models.*

interface HomeApiService {

    @GET("DoctorsCallRequest/CountDoctorsCallRequest")
    suspend fun getListStatus(
        @Query(
            "Token",
        ) token: String
    ): Response<List<ResponseCountStatusOrders>>

    @GET("DoctorsCallRequest/ListDostorsCallRequests?quantity=100&start=0")
    suspend fun getOrders(
        @Query(
            "Token",
        ) token: String,
        @Query(
            "Status",
        ) status: String
    ): Response<List<ResponseListOrders>>

    @GET("DoctorsCallRequest/DoctorsCallRequest")
    suspend fun getOrder(
        @Query(
            "Token",
        ) token: String,
        @Query(
            "RequestGUID",
        ) requestId: String
    ): Response<ResponseOrder>

    @POST("DoctorsCallRequest/DoctorsCallRequest")
    suspend fun updateOrder(
        @Body body: ResponseOrder,
    ): Response<String>

    @POST("DoctorsCallRequest/AcceptRejectRequest")
    suspend fun acceptRejectRequest(
        @Body body: HashMap<String, String>
    ): Response<String>

    @POST("DoctorsCallRequest/ChangeStatus")
    suspend fun changeStatus(
        @Body body: HashMap<String, String>
    ): Response<String>

    @GET("DoctorsCallRequest/ListLaboratories")
    suspend fun getListLaboratories(
        @Query(
            "Token",
        ) token: String
    ): Response<List<ResponseLaboratory>>

    @POST("DoctorsCallRequest/CreatePayment")
    suspend fun createPayment(
        @Body body: RequestCreatePayment
    ): Response<ResponseCreatePayment>

    @GET("DoctorsCallRequest/CreatePayment")
    suspend fun getQrOrLinkPayment(
        @Query(
            "Token",
        ) token: String,
        @Query(
            "PaymentType",
        ) paymentType: String,
        @Query(
            "PaymentGUID",
        ) paymentGUID: String
    ): Response<ResponseCreatePayment>

    @GET("DoctorsCallRequest/ListMedicalServices")
    suspend fun getListMedicalServices(
        @Query(
            "Token",
        ) token: String,
        @Query(
            "GUIDLaboratory",
        ) laboratoryId: String,
        @Query(
            "RequestGUID",
        ) requestId: String
    ): Response<List<ResponseMedicalService>>

    @GET("DoctorsCallRequest/ListFileTypes")
    suspend fun getListFileTypes(
        @Query(
            "Token",
        ) token: String
    ): Response<List<ResponseListTypeFiles>>

    @GET("DoctorsCallRequest/UploadFile")
    suspend fun getFilesFromOrder(
        @Query(
            "Token",
        ) token: String,
        @Query(
            "RequestGUID",
        ) requestId: String
    ): Response<List<ResponseListTypeFiles>>

    @POST("DoctorsCallRequest/UploadFile")
    suspend fun uploadFile(
        @Body body: RequestUploadFile
    ): Response<String>

    @GET("DoctorsCallRequest/DiagnosisSelection")
    suspend fun searchDiagnosis(
        @Query(
            "Token",
        ) token: String,
        @Query(
            "text",
        ) searchText: String
    ): Response<List<ResponseDiagnosis>>

    @POST("DoctorsCallRequest/CreateRequest")
    suspend fun createOrder(@Body body: RequestCreateOrder): Response<String>

    @GET("DoctorsCallRequest/ListInsuranceCompany")
    suspend fun getListInsuranceCompanies(
        @Query(
            "Token",
        ) token: String
    ): Response<List<ResponseInsuranceCompanies>>
}