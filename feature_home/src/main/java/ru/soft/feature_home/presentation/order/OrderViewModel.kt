package ru.soft.feature_home.presentation.order

import android.annotation.*
import android.content.*
import android.net.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.data.*
import ru.soft.feature_home.data.models.*
import java.text.*
import java.util.*
import java.util.concurrent.atomic.*
import javax.inject.*

open class OrderViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sharedPreferences: SharedPreferences,
) : BaseViewModel() {

    private val loadingCounter = AtomicInteger(0)
    private val _loading = MutableStateFlow(0)
    val loading: Flow<Boolean>
        get() = _loading.map { it != 0 }

    private fun incrementLoadingCounter() {
        _loading.tryEmit(loadingCounter.incrementAndGet())
    }

    private fun decrementLoadingCounter() {
        _loading.tryEmit(loadingCounter.decrementAndGet())
    }

    private val _isErrorToken = MutableSharedFlow<Boolean>()
    val isErrorToken = _isErrorToken.asSharedFlow()

    private val _errorChangeStatus = MutableSharedFlow<String>()
    val errorChangeStatus = _errorChangeStatus.asSharedFlow()

    private val _order = MutableStateFlow<ResponseOrder?>(null)
    val order = _order.asStateFlow()

    private val _laboratories = MutableStateFlow<List<ResponseLaboratory>?>(null)
    val laboratories = _laboratories.asStateFlow()

    private val _servicesList = MutableSharedFlow<List<ResponseMedicalService>?>()
    val servicesList = _servicesList.asSharedFlow()

    private val _successRejectStatus = MutableStateFlow<Boolean?>(null)
    val successRejectStatus = _successRejectStatus.asStateFlow()

    private val _successCreatePayment = MutableStateFlow<String?>(null)
    val successCreatePayment = _successCreatePayment.asStateFlow()

    private val _successCreateOrder = MutableStateFlow<String?>(null)
    val successCreateOrder = _successCreateOrder.asStateFlow()

    private val _listTypeFiles = MutableSharedFlow<List<ResponseListTypeFiles>>()
    val listTypeFiles = _listTypeFiles.asSharedFlow()

    private val _searchDiagnosis = MutableSharedFlow<List<ResponseDiagnosis>>()
    val searchDiagnosis = _searchDiagnosis.asSharedFlow()

    private val _successInsuranceCompanies = MutableStateFlow<List<ResponseInsuranceCompanies>?>(null)
    val successInsuranceCompanies = _successInsuranceCompanies.asStateFlow()

    private val _successGetQrOrLinkPayment = MutableStateFlow<ResponseCreatePayment?>(null)
    val successGetQrOrLinkPayment = _successGetQrOrLinkPayment.asStateFlow()

    var selectedLaboratory: ResponseLaboratory? = null
    var firstListMedicalServices: MutableList<ResponseMedicalService> = mutableListOf()
    var secondListMedicalServices: MutableList<ResponseMedicalService> = mutableListOf()

    var selectedInsuranceCompany: ResponseInsuranceCompanies? = null

    fun getListServices(laboratoryId: String) {
        launchJob(
            useCase = {
                homeRepository.getListMedicalServices(
                    token = getToken(),
                    requestId = _order.value?.requestGUID.toString(),
                    laboratoryId = laboratoryId

                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _servicesList.emit(it.data)
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun getListLaboratories() {
        launchJob(
            useCase = {
                homeRepository.getListLaboratories(
                    token = getToken()
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _laboratories.emit(it.data)
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun getOrder(requestId: String) {
        launchJob(
            useCase = {
                homeRepository.getOrder(
                    requestId = requestId,
                    token = getToken()
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        firstListMedicalServices.clear()
                        it.data?.medicalServices?.filter {
                            it.paymentByThePatient == false
                        }?.let { it1 -> firstListMedicalServices.addAll(it1) }
                        secondListMedicalServices.clear()
                        it.data?.medicalServices?.filter {
                            it.paymentByThePatient == true
                        }?.let { it1 -> secondListMedicalServices.addAll(it1) }
                        _order.emit(it.data)
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun acceptOrder() {
        launchJob(
            useCase = {
                homeRepository.acceptRejectRequest(
                    status = "Accept",
                    requestId = order.value?.requestGUID.toString(),
                    token = getToken()
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successMessage.emit(it.data)
                        delay(1000)
                        getOrder(order.value?.requestGUID.toString())
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )

    }

    fun rejectOrder() {
        launchJob(
            useCase = {
                homeRepository.acceptRejectRequest(
                    status = "Reject",
                    requestId = order.value?.requestGUID.toString(),
                    token = getToken()
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successRejectStatus.emit(true)
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun changeStatus() {
        launchJob(
            useCase = {
                homeRepository.changeStatus(
                    status = getStatus(order.value?.status.toString()),
                    requestId = order.value?.requestGUID.toString(),
                    token = getToken()
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successMessage.emit(it.data)
                        delay(1000)
                        getOrder(order.value?.requestGUID.toString())
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    private fun getStatus(status: String): String {
        return when (status) {
            "Принята" -> "ВрачВыехал"
            "Врач выехал" -> "НачатПрием"
            "Начат прием" -> "ЗавершенПрием"
            "Завершен прием" -> "Закрыта"
            else -> {
                ""
            }
        }
    }

    fun getSelectedLaboratory(index: Int) {
        selectedLaboratory = laboratories.value?.get(index)
        firstListMedicalServices.clear()
        secondListMedicalServices.clear()
        viewModelScope.launch {
            _order.value = _order.value?.copy(
                gUIDLaboratory = selectedLaboratory?.gUID,
                medicalServices = firstListMedicalServices + secondListMedicalServices
            )
        }
    }

    fun removeFromFirstList(it: ResponseMedicalService) {
        firstListMedicalServices.remove(it)
        viewModelScope.launch {
            _order.value = _order.value?.copy(
                medicalServices = firstListMedicalServices + secondListMedicalServices
            )
        }
    }

    fun removeFromSecondList(it: ResponseMedicalService) {
        secondListMedicalServices.remove(it)
        viewModelScope.launch {
            _order.value = _order.value?.copy(
                medicalServices = firstListMedicalServices + secondListMedicalServices
            )
        }
    }

    fun updateOrder(isNeedCreateOrder: Boolean) {
        val order = _order.value
        order?.token = getToken()
        launchJob(
            useCase = {
                homeRepository.updateOrder(
                    body = order!!
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successMessage.emit("Данные изменены успешно")
                        if (isNeedCreateOrder) {
                            createPayment()
                        }
                        _order.value?.requestGUID?.let { it1 -> getOrder(it1) }
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun addMedicalService() {
        viewModelScope.launch {
            _order.value = _order.value?.copy(
                medicalServices = firstListMedicalServices + secondListMedicalServices
            )
        }
    }

    private fun createPayment() {
        launchJob(
            useCase = {
                homeRepository.createPayment(
                    body = RequestCreatePayment(
                        requestGUID = order.value?.requestGUID,
                        sum = (secondListMedicalServices.sumOf { it.price ?: 0 }).toString(),
                        token = getToken(),
                    )
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successMessage.emit("Данные изменены успешно")
                        _successCreatePayment.emit(it.data?.paymentGUID)
                        order.value?.requestGUID?.let { it1 -> getOrder(it1) }
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    private fun updatePatient(
        birthday: String? = null,
        fIO: String? = null,
        gUID: String? = null,
        passportDateOfIssue: String? = null,
        passportIssuedBy: String? = null,
        passportNumber: String? = null,
        patientComment: String? = null,
        registrationAddress: String? = null,
        sNILS: String? = null
    ) {
        viewModelScope.launch {
            val currentPatient = _order.value?.patient ?: return@launch

            _order.value = _order.value?.copy(
                patient = currentPatient.copy(
                    birthday = birthday ?: currentPatient.birthday,
                    fIO = fIO ?: currentPatient.fIO,
                    gUID = gUID ?: currentPatient.gUID,
                    passportDateOfIssue = passportDateOfIssue ?: currentPatient.passportDateOfIssue,
                    passportIssuedBy = passportIssuedBy ?: currentPatient.passportIssuedBy,
                    passportNumber = passportNumber ?: currentPatient.passportNumber,
                    patientComment = patientComment ?: currentPatient.patientComment,
                    registrationAddress = registrationAddress ?: currentPatient.registrationAddress,
                    sNILS = sNILS ?: currentPatient.sNILS
                )
            )
        }
    }

    fun setPassport(passportNumber: String? = null) {
        updatePatient(passportNumber = passportNumber)
    }

    fun setPassportWho(passportIssuedBy: String? = null) {
        updatePatient(passportIssuedBy = passportIssuedBy)
    }

    fun setPassportAddress(registrationAddress: String? = null) {
        updatePatient(registrationAddress = registrationAddress)
    }

    @SuppressLint("SimpleDateFormat")
    fun setPassportDate(date: Long) {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Date(date))
        updatePatient(passportDateOfIssue = formattedDate)
    }


    @SuppressLint("SimpleDateFormat")
    fun setComment(it: String) {
        viewModelScope.launch {
            _order.value = _order.value?.copy(
                commentPayment = it
            )
        }
    }

    fun generatePayment(type: String) {
        val order = _order.value
        order?.token = getToken()
        launchJob(
            useCase = {
                homeRepository.updateOrder(
                    body = _order.value!!
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        generatePaymentLinkOrQr(type = type)
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    private fun generatePaymentLinkOrQr(type: String) {
        launchJob(
            useCase = {
                homeRepository.getQrOrLinkPayment(
                    body = RequestCreatePayment(
                        paymentGUID = _order.value?.paymentGUID,
                        paymentType = if (type == "QR") "QRCode" else "PaymentLink",
                        token = getToken()
                    )
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successMessage.emit("Успех")
                        _successGetQrOrLinkPayment.emit(it.data)
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun getFilesFromOrder() {
        val listTypeFiles = mutableListOf<ResponseListTypeFiles>()
        launchJob(
            useCase = {
                homeRepository.getListFileTypes(
                    token = getToken()
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { it1 -> listTypeFiles.addAll(it1) }
                        launchJob(
                            useCase = {
                                homeRepository.getFilesFromOrder(
                                    token = getToken(),
                                    requestId = getRequestId()
                                )
                            },
                            resultAction = { res ->
                                when (res.status) {
                                    Status.SUCCESS -> {
                                        listTypeFiles.map { map ->
                                            map.isHaveFile = res.data?.firstOrNull {
                                                it.gUID == map.gUID
                                            } != null
                                        }
                                        _listTypeFiles.emit(listTypeFiles)
                                    }

                                    Status.ERROR -> {
                                        if (it.statusCode == 401) {
                                            sharedPreferences.edit().putString("TOKEN", "").apply()
                                            _isErrorToken.emit(true)
                                        }
                                        _errorChangeStatus.emit(it.message.toString())
                                    }
                                }
                            },
                            incrementLoadingCounter = ::incrementLoadingCounter,
                            decrementLoadingCounter = ::decrementLoadingCounter
                        )
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    private fun getToken() = sharedPreferences.getString("TOKEN", "") ?: ""

    private fun getRequestId() = _order.value?.requestGUID ?: ""

    fun addFile(file: String, type: ResponseListTypeFiles) {
        launchJob(
            useCase = {
                homeRepository.uploadFile(
                    body = RequestUploadFile(
                        requestGUID = getRequestId(),
                        token = getToken(),
                        fileTypeGUID = type.gUID,
                        fileName = Date().time.toString() + ".jpg",
                        fileTypeDescription = "",
                        fileBase64 = file,
                    )
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successMessage.emit("Успех")
                        getFilesFromOrder()
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun setCommentPhoto(it: String) {
        viewModelScope.launch {
            _order.value = _order.value?.copy(
                commentPhoto = it
            )
        }
    }

    fun setDiagnosisComment(it: String) {
        viewModelScope.launch {
            val diagnosis = _order.value?.iCDDiagnosis ?: return@launch
            _order.value = _order.value?.copy(
                iCDDiagnosis = diagnosis.copy(
                    appointment = it
                )
            )
        }
    }

    fun setDiagnosisDate(it: String) {
        viewModelScope.launch {
            val diagnosis = _order.value?.iCDDiagnosis ?: return@launch
            _order.value = _order.value?.copy(
                iCDDiagnosis = diagnosis.copy(
                    readmissionDate = it
                )
            )
        }
    }

    fun searchDiagnosis(it: String) {
        launchJob(
            useCase = {
                homeRepository.searchDiagnosis(
                    token = getToken(),
                    searchText = it
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { it1 -> _searchDiagnosis.emit(it1) }
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun setDiagnosis(item: ResponseDiagnosis) {
        viewModelScope.launch {
            val diagnosis = _order.value?.iCDDiagnosis ?: return@launch
            _order.value = _order.value?.copy(
                iCDDiagnosis = diagnosis.copy(
                    gUID = item.gUID
                )
            )
        }
    }

    fun setInspectionField(detail: ResponseOrder.InspectionDetail) {
        viewModelScope.launch {
            val inspectionDetails = _order.value?.inspectionDetails ?: return@launch
            inspectionDetails.forEach {
                if (it?.name == detail.name) {
                    it?.value = detail.value
                }
            }
            _order.value = _order.value?.copy(
                inspectionDetails = inspectionDetails
            )
        }
    }

    fun setFromDateLn(it: String) {
        viewModelScope.launch {
            val sickLeave = _order.value?.sickLeave ?: return@launch
            _order.value = _order.value?.copy(
                sickLeave = sickLeave.copy(
                    sickLeaveStartDate = it
                )
            )
        }
    }

    fun setToDateLn(it: String) {
        viewModelScope.launch {
            val sickLeave = _order.value?.sickLeave ?: return@launch
            _order.value = _order.value?.copy(
                sickLeave = sickLeave.copy(
                    sickLeaveEndDate = it
                )
            )
        }
    }

    fun setSnils(it: String) {
        viewModelScope.launch {
            val sickLeave = _order.value?.sickLeave ?: return@launch
            val patient = _order.value?.patient ?: return@launch
            _order.value = _order.value?.copy(
                sickLeave = sickLeave.copy(
                    sNILS = it
                ),
                patient = patient.copy(
                    sNILS = it
                ),
            )
        }
    }

    fun setCommentLn(it: String) {
        viewModelScope.launch {
            val sickLeave = _order.value?.sickLeave ?: return@launch
            _order.value = _order.value?.copy(
                sickLeave = sickLeave.copy(
                    commentSickLeave = it
                )
            )
        }
    }

    fun setSickLeaveNotRequired(it: Boolean) {
        viewModelScope.launch {
            val sickLeave = _order.value?.sickLeave ?: return@launch
            _order.value = _order.value?.copy(
                sickLeave = sickLeave.copy(
                    sickLeaveNotRequired = it
                )
            )
        }
    }

    fun getStatusesLn(): List<String> {
        return listOf<String>(
            "Открыт",
            "Закрыт",
            "Продлить",
            "ОткрытВДругойОрганизацииВыдатьПродление",
            "ВрачебнаяКомиссия"
        )
    }


    fun getStatusesLnList(): List<String> {
        return listOf<String>(
            "Открыт",
            "Закрыт",
            "Продлить",
            "Открыт в другой организации (выдать продление)",
            "Врачебная комиссия"
        )
    }

    fun selectStatusSick(index: Int) {
        viewModelScope.launch {
            val sickLeave = _order.value?.sickLeave ?: return@launch
            _order.value = _order.value?.copy(
                sickLeave = sickLeave.copy(
                    sickLeaveStatus = getStatusesLn()[index]
                )
            )
        }
    }

    fun createOrder(
        fio: String,
        phone: String,
        email: String,
        complaints: String,
        insurancePolicyNumber: String,
        comment: String
    ) {
        launchJob(
            useCase = {
                homeRepository.createOrder(
                    body = RequestCreateOrder(
                        comment = comment,
                        token = getToken(),
                        insuranceGUID = selectedInsuranceCompany?.gUID,
                        insurancePolicyNumber = insurancePolicyNumber,
                        patientEmail = email,
                        patientFIO = fio,
                        patientPhone = phone,
                        сomplaints = complaints
                    )
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successCreateOrder.emit("Данные сохранены")
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }

    fun getListInsuranceCompanies() {
        launchJob(
            useCase = {
                homeRepository.getListInsuranceCompanies(
                    token = getToken()
                )
            },
            resultAction = {
                when (it.status) {
                    Status.SUCCESS -> {
                        _successInsuranceCompanies.emit(it.data)
                    }

                    Status.ERROR -> {
                        if (it.statusCode == 401) {
                            sharedPreferences.edit().putString("TOKEN", "").apply()
                            _isErrorToken.emit(true)
                        }
                        _errorChangeStatus.emit(it.message.toString())
                    }
                }
            },
            incrementLoadingCounter = ::incrementLoadingCounter,
            decrementLoadingCounter = ::decrementLoadingCounter
        )
    }
}


open class OrderViewModelFactory @Inject constructor(
    private val homeRepository: HomeRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(homeRepository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}