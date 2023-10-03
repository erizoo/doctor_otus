package ru.soft.feature_home.data

import kotlinx.coroutines.flow.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.data.models.*
import javax.inject.*

class HomeRepositoryImpl @Inject constructor(private val homeApiService: HomeApiService) :
    HomeRepository {

    override suspend fun getListStatus(token: String): Flow<Resource<List<ResponseCountStatusOrders>>> =
        flow {
            val response = homeApiService.getListStatus(token = token)
            emit(handleResponse(response))
        }

    override suspend fun getOrders(
        token: String,
        status: String
    ): Flow<Resource<List<ResponseListOrders>>> =
        flow {
            val response = homeApiService.getOrders(token = token, status = status)
            emit(handleResponse(response))
        }

    override suspend fun getOrder(
        token: String,
        requestId: String
    ): Flow<Resource<ResponseOrder>> =
        flow {
            val response = homeApiService.getOrder(token = token, requestId = requestId)
            emit(handleResponse(response))
        }

    override suspend fun updateOrder(
        body: ResponseOrder
    ): Flow<Resource<String>> =
        flow {
            val response = homeApiService.updateOrder(body = body)
            emit(handleResponse(response))
        }

    override suspend fun changeStatus(
        token: String,
        requestId: String,
        status: String,
    ): Flow<Resource<String>> =
        flow {
            val hashMap = hashMapOf<String, String>()
            hashMap["Token"] = token
            hashMap["Status"] = status
            hashMap["RequestGUID"] = requestId
            val response = homeApiService.changeStatus(body = hashMap)
            emit(handleResponse(response))
        }

    override suspend fun acceptRejectRequest(
        token: String,
        requestId: String,
        status: String,
    ): Flow<Resource<String>> =
        flow {
            val hashMap = hashMapOf<String, String>()
            hashMap["Token"] = token
            hashMap["Solution"] = status
            hashMap["RequestGUID"] = requestId
            val response = homeApiService.acceptRejectRequest(body = hashMap)
            emit(handleResponse(response))
        }

    override suspend fun getListLaboratories(
        token: String
    ): Flow<Resource<List<ResponseLaboratory>>> =
        flow {
            val response = homeApiService.getListLaboratories(token = token)
            emit(handleResponse(response))
        }

    override suspend fun createPayment(
        body: RequestCreatePayment
    ): Flow<Resource<ResponseCreatePayment>> =
        flow {
            val response = homeApiService.createPayment(body = body)
            emit(handleResponse(response))
        }

    override suspend fun getQrOrLinkPayment(
        body: RequestCreatePayment
    ): Flow<Resource<ResponseCreatePayment>> =
        flow {
            val response = homeApiService.getQrOrLinkPayment(
                token = body.token!!,
                paymentType = body.paymentType!!,
                paymentGUID = body.paymentGUID!!
            )
            emit(handleResponse(response))
        }

    override suspend fun getListMedicalServices(
        token: String,
        laboratoryId: String,
        requestId: String,
    ): Flow<Resource<List<ResponseMedicalService>>> =
        flow {
            val response =
                homeApiService.getListMedicalServices(
                    token = token, laboratoryId = laboratoryId,
                    requestId = requestId
                )
            emit(handleResponse(response))
        }

    override suspend fun getListFileTypes(
        token: String
    ): Flow<Resource<List<ResponseListTypeFiles>>> =
        flow {
            val response =
                homeApiService.getListFileTypes(
                    token = token
                )
            emit(handleResponse(response))
        }

    override suspend fun getFilesFromOrder(
        token: String,
        requestId: String,
    ): Flow<Resource<List<ResponseListTypeFiles>>> =
        flow {
            val response =
                homeApiService.getFilesFromOrder(
                    token = token,
                    requestId = requestId
                )
            emit(handleResponse(response))
        }


    override suspend fun uploadFile(
        body: RequestUploadFile
    ): Flow<Resource<String>> =
        flow {
            val response =
                homeApiService.uploadFile(
                    body = body
                )
            emit(handleResponse(response))
        }

    override suspend fun searchDiagnosis(
        token: String,
        searchText: String,
    ): Flow<Resource<List<ResponseDiagnosis>>> =
        flow {
            val response =
                homeApiService.searchDiagnosis(
                    token = token,
                    searchText = searchText
                )
            emit(handleResponse(response))
        }

    override suspend fun createOrder(body: RequestCreateOrder): Flow<Resource<String>> =
        flow {
            val response =
                homeApiService.createOrder(
                    body = body
                )
            emit(handleResponse(response))
        }

    override suspend fun getListInsuranceCompanies(token: String): Flow<Resource<List<ResponseInsuranceCompanies>>> =
        flow {
            val response =
                homeApiService.getListInsuranceCompanies(
                    token = token,
                )
            emit(handleResponse(response))
        }
}