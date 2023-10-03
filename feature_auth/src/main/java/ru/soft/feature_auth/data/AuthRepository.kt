package ru.soft.feature_auth.data

import kotlinx.coroutines.flow.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_auth.data.models.*

interface AuthRepository {

    suspend fun login(phone: String): Flow<Resource<List<ResponseSentPhoneModelItem>>>

    suspend fun sendFirebaseToken(body: RequestTokenFirebase): Flow<Resource<String>>

    suspend fun checkSmsCode(
        phone: String,
        smsCode: String
    ): Flow<Resource<List<ResponseAuthTokenModel>>>

}