package ru.soft.feature_home.data

import kotlinx.coroutines.flow.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.data.models.*

interface HomeRepository {

    suspend fun getListStatus(
        token: String
    ): Flow<Resource<List<ResponseCountStatusOrders>>>

    suspend fun getOrders(
        token: String,
        status: String
    ): Flow<Resource<List<ResponseListOrders>>>

    suspend fun getOrder(
        token: String,
        requestId: String
    ): Flow<Resource<ResponseOrder>>

    suspend fun updateOrder(
        body: ResponseOrder
    ): Flow<Resource<String>>

    suspend fun createPayment(
        body: RequestCreatePayment
    ): Flow<Resource<ResponseCreatePayment>>

    suspend fun getQrOrLinkPayment(
        body: RequestCreatePayment
    ): Flow<Resource<ResponseCreatePayment>>

    suspend fun changeStatus(
        token: String,
        requestId: String,
        status: String
    ): Flow<Resource<String>>

    suspend fun acceptRejectRequest(
        token: String,
        requestId: String,
        status: String
    ): Flow<Resource<String>>

    suspend fun getListLaboratories(
        token: String
    ): Flow<Resource<List<ResponseLaboratory>>>

    suspend fun getListMedicalServices(
        token: String,
        laboratoryId: String,
        requestId: String
    ): Flow<Resource<List<ResponseMedicalService>>>

    suspend fun getListFileTypes(
        token: String
    ): Flow<Resource<List<ResponseListTypeFiles>>>

    suspend fun getFilesFromOrder(
        token: String,
        requestId: String
    ): Flow<Resource<List<ResponseListTypeFiles>>>

    suspend fun uploadFile(
        body: RequestUploadFile
    ): Flow<Resource<String>>

    suspend fun searchDiagnosis(
        token: String,
        searchText: String
    ): Flow<Resource<List<ResponseDiagnosis>>>

    suspend fun createOrder(
        body: RequestCreateOrder
    ): Flow<Resource<String>>

    suspend fun getListInsuranceCompanies(
        token: String
    ): Flow<Resource<List<ResponseInsuranceCompanies>>>

}