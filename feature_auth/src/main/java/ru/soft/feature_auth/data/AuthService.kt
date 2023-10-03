package ru.soft.feature_auth.data

import kotlinx.coroutines.flow.*
import retrofit2.*
import retrofit2.http.*
import ru.soft.feature_auth.data.models.*

interface AuthService {

    @GET("Authorization/ValidationCode")
    suspend fun login(
        @Query(
            "Phone",
            encoded = true
        ) phone: String
    ): Response<List<ResponseSentPhoneModelItem>>

    @GET("Authorization/Token")
    suspend fun checkSmsCode(
        @Query(
            "Phone",
            encoded = true
        ) phone: String,
        @Query(
            "ValidationCode",
            encoded = true
        ) smsCode: String
    ): Response<List<ResponseAuthTokenModel>>

    @POST("Authorization/PostTokenFairBase")
    suspend fun sendFirebaseToken(
        @Body body: RequestTokenFirebase
    ): Response<String>
}