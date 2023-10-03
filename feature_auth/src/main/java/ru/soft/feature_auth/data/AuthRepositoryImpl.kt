package ru.soft.feature_auth.data

import kotlinx.coroutines.flow.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_auth.data.models.*
import javax.inject.*

class AuthRepositoryImpl @Inject constructor(private var authService: AuthService) :
    AuthRepository {

    override suspend fun login(phone: String): Flow<Resource<List<ResponseSentPhoneModelItem>>> =
        flow {
            val response = authService.login(phone)
            emit(handleResponse(response))
        }

    override suspend fun sendFirebaseToken(body: RequestTokenFirebase): Flow<Resource<String>> =
        flow {
            val response = authService.sendFirebaseToken(body = body)
            emit(handleResponse(response))
        }

    override suspend fun checkSmsCode(
        phone: String,
        smsCode: String
    ): Flow<Resource<List<ResponseAuthTokenModel>>> = flow {
        val response = authService.checkSmsCode(phone = phone, smsCode = smsCode)
        emit(handleResponse(response))
    }
}