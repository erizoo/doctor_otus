package ru.soft.feature_home.presentation.order

import android.*
import android.annotation.*
import android.app.*
import android.content.*
import android.graphics.*
import android.net.*
import android.os.*
import android.provider.*
import android.text.*
import android.text.style.*
import android.util.*
import android.util.Base64
import android.view.*
import android.widget.*
import androidx.activity.result.contract.*
import androidx.core.content.*
import androidx.core.view.*
import androidx.core.widget.*
import androidx.fragment.app.*
import androidx.lifecycle.*
import coil.*
import coil.request.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import com.google.android.material.datepicker.*
import com.karumi.dexter.*
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.single.*
import com.skydoves.powerspinner.*
import kotlinx.coroutines.*
import ru.soft.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_home.R
import ru.soft.feature_home.data.models.*
import ru.soft.feature_home.databinding.*
import ru.soft.feature_home.di.*
import ru.soft.feature_home.presentation.*
import ru.soft.main_api.*
import ru.soft.ui_kit.views.*
import java.io.*
import java.net.*
import java.nio.charset.*
import java.text.*
import java.util.*
import javax.inject.*


class OrderFragment : BaseFragment(R.layout.fragment_order) {

    private val binding by viewBinding(FragmentOrderBinding::bind)
    private val requestId: String? by argument(ARG_REQUEST_ID, null)
    private var selectedFirstButton = true
    private var selectedSecondButton = true
    private var latestTmpUri: Uri? = null
    private var selectedTypeFile: ResponseListTypeFiles? = null

    @Inject
    lateinit var viewModelFactory: OrderViewModelFactory
    private val viewModel by viewModels<OrderViewModel> {
        viewModelFactory
    }

    @Inject
    lateinit var mainMediator: MainMediator

    @Inject
    lateinit var authMediator: AuthMediator

    @Inject
    lateinit var router: Router

    private val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")
            .build()

    private val servicesAdapter by lazy {
        MultiBindingAdapter(
            medicalServicesWithoutPrice(
                onClick = {

                },
                onClickDeleted = {
                    if (viewModel.order.value?.paymentGUID.isNullOrEmpty()) {
                        viewModel.removeFromFirstList(it)
                    }
                }
            )
        )
    }

    private val secondServicesAdapter by lazy {
        MultiBindingAdapter(
            medicalServicesWithPrice(
                onClick = {

                }, onClickDeleted = {
                    if (viewModel.order.value?.paymentGUID.isNullOrEmpty()) {
                        viewModel.removeFromSecondList(it)
                    }
                }
            )
        )
    }

    private val filesAdapter by lazy {
        MultiBindingAdapter(
            filesAdapter(
                onClick = {
                    pickFiles(it)
                }
            )
        )
    }

    private val inspectionAdapter by lazy {
        MultiBindingAdapter(
            inspections(
                afterTextChanged = {
                    viewModel.setInspectionField(it)
                }
            )
        )
    }

    companion object {
        fun newInstance(
            requestId: String
        ): OrderFragment {
            val fragment = OrderFragment()
            val args = Bundle()
            args.putString(ARG_REQUEST_ID, requestId)
            fragment.arguments = args
            return fragment
        }

        const val FILE_PROVIDER_AUTHORITY = ".provider"
        const val PACKAGE = "ru.soft.med"
        const val IMAGE_CONTENT_TYPE = "image/*"
        private const val TMP_FILE_NAME = "tmp_image_file"
        private const val TMP_FILE_FORMAT = ".jpg"
        const val ARG_REQUEST_ID = "ARG_REQUEST_ID"
        const val TAG = "OrderFragment"
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    val bitmap = getBitmapFromUri(requireContext(), uri)
                    if (bitmap != null) {
                        val resizedBitmap = resizeBitmap(bitmap, 1024, 768)
                        val base64String = bitmapToBase64(resizedBitmap)
                        selectedTypeFile?.let {
                            viewModel.addFile(
                                file = base64String,
                                type = it
                            )
                        }
                    }
                }
            }
        }

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = getBitmapFromUri(requireContext(), it)
                if (bitmap != null) {
                    val resizedBitmap = resizeBitmap(bitmap, 1024, 768)
                    val base64String = bitmapToBase64(resizedBitmap)
                    selectedTypeFile?.let { it1 ->
                        viewModel.addFile(
                            file = base64String,
                            type = it1
                        )
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeComponent.create((requireActivity().application as AppWithFacade).getFacade())
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initClickers()
        initObservers()

        viewModel.getListLaboratories()
        requestId?.let {
            viewModel.getOrder(
                requestId = it
            )
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "NotifyDataSetChanged")
    private fun initObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.successMessage.collect {
                if (it != null) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.successRejectStatus.collect {
                if (it != null && it == true) {
                    router.exit()
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.successCreatePayment.collect {
                if (!it.isNullOrEmpty()) {
                    binding.rbPay.makeVisible()
                    binding.rbPay.isChecked = true
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.successGetQrOrLinkPayment.collect {
                if (it != null) {
                    if (it.paymentLink.isNullOrEmpty() && !it.qr.isNullOrEmpty()) {
                        binding.layoutPayment.groupQr.makeVisible()
                        binding.layoutPayment.groupLink.makeGone()
                        binding.layoutPayment.groupField.makeGone()
                        binding.layoutPayment.tvStatusQr.text = "Статус:\n${it.qrStatus}"
                        val bitmap = it.qr.convertStringToBitmap()
                        binding.layoutPayment.ivQr.setImageBitmap(bitmap)
                    } else if (!it.paymentLink.isNullOrEmpty() && it.qr.isNullOrEmpty()) {
                        binding.layoutPayment.groupLink.makeVisible()
                        binding.layoutPayment.groupQr.makeGone()
                        binding.layoutPayment.groupField.makeGone()
                        val mSpannableString = SpannableString(it.paymentLink)
                        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
                        binding.layoutPayment.tvLink.text = mSpannableString
                        binding.layoutPayment.tvStatusLink.text = "Статус:\n${it.paymentLinkStatus}"
                    }
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.loading.collect {
                binding.layoutProgress.layoutProgress.isVisible = it
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.errorChangeStatus.collect {
                showDialogError(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.order.collect {
                if (it != null) {
                    binding.tvTitle.text =
                        "N ${it.number} от ${it.date?.parseDate(SimpleDateFormat("dd.MM.yy"))}\nСтатус: ${it.status}"
                    binding.rbPay.isVisible = !it.paymentGUID.isNullOrEmpty()
                    parseOrderInformation(it)
                    parseMedicalServicesInformation(it)
                    parsePaymentInformation(it)
                    parsePhotoInformation(it)
                    parseDiagnosisInformation(it)
                    parseInspection(it)
                    parseLn(it)
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.isErrorToken.collect {
                if (it) {
                    mainMediator.hideBottomBar(requireContext())
                    authMediator.startAuthScreen()
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.listTypeFiles.collect {
                if (it.isNotEmpty()) {
                    filesAdapter.items = it
                    filesAdapter.notifyDataSetChanged()
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.servicesList.collect {
                if (it?.isNotEmpty() == true) {
                    val dialog = DialogListMedicalServices.newInstance(
                        services = it
                    )
                    dialog.setCallback(object : DialogListMedicalServices.Callback {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onItemSelected(item: ResponseMedicalService) {
                            if (selectedFirstButton) {
                                viewModel.firstListMedicalServices.add(item)
                                item.paymentByThePatient = false
                                viewModel.addMedicalService()
                                servicesAdapter.items = viewModel.firstListMedicalServices
                                servicesAdapter.notifyDataSetChanged()
                            } else {
                                viewModel.secondListMedicalServices.add(item)
                                item.paymentByThePatient = true
                                viewModel.addMedicalService()
                                secondServicesAdapter.items = viewModel.secondListMedicalServices
                                secondServicesAdapter.notifyDataSetChanged()
                            }
                            updatePrice()
                        }
                    })
                    dialog.show(parentFragmentManager, DialogListMedicalServices.TAG)
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.laboratories.collect {
                if (it?.isNotEmpty() == true) {
                    binding.layoutMedicalServices.listDropDown.apply {
                        this.lifecycleOwner = viewLifecycleOwner
                        this.setItems(it.map {
                            it.name
                        })
                        this.arrowGravity = SpinnerGravity.END
                        this.arrowTint =
                            ContextCompat.getColor(requireContext(), ru.soft.ui_atoms.R.color.black)
                        this.spinnerPopupAnimation = SpinnerAnimation.NORMAL
                        this.showDivider = true
                        this.gravity = Gravity.CENTER_VERTICAL
                        this.dividerColor = Color.WHITE
                        this.dividerSize = 2
                        this.selectItemByIndex(0)
                    }
                }
            }
        }
    }

    private fun parseInspection(it: ResponseOrder) {
        it.let { order ->
            binding.layoutInspection.tvNamePatient.text = order.patient?.fIO
            inspectionAdapter.items =
                order.inspectionDetails as List<ResponseOrder.InspectionDetail>
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun parseLn(it: ResponseOrder) {
        it.let { order ->
            binding.layoutLn.tvNamePatient.text = order.patient?.fIO
            binding.layoutLn.listDropDown.selectItemByIndex(
                viewModel.getStatusesLn().indexOf(order.sickLeave?.sickLeaveStatus)
            )
            binding.layoutLn.etSnils.setTextWithoutChangeEvents(order.sickLeave?.sNILS)
            binding.layoutLn.etValue.setTextWithoutChangeEvents(order.sickLeave?.commentSickLeave)
            binding.layoutLn.checkbox.isChecked = order.sickLeave?.sickLeaveNotRequired == true
            if (!order.previousSickLeave?.sickLeaveNumber.isNullOrEmpty()
                && !order.previousSickLeave?.sickLeaveStartDate.isNullOrEmpty()
                && !order.previousSickLeave?.sickLeaveStatus.isNullOrEmpty()
                && !order.previousSickLeave?.sickLeaveEndDate.isNullOrEmpty()
            ) {
                binding.layoutLn.tvStatus.text =
                    "${
                        order.previousSickLeave?.sickLeaveStatus
                    } Л/Н № ${order.previousSickLeave?.sickLeaveNumber}\n" +
                            "с ${
                                order.previousSickLeave?.sickLeaveStartDate?.parseDate(
                                    SimpleDateFormat("dd.MM.yy")
                                )
                            } по ${
                                order.previousSickLeave?.sickLeaveEndDate?.parseDate(
                                    SimpleDateFormat("dd.MM.yy")
                                )
                            }"
            }
            order.sickLeave?.sickLeaveStartDate?.let { it1 ->
                binding.layoutLn.ivFrom.setText(
                    it1.parseDate(
                        SimpleDateFormat("dd.MM.yyyy")
                    )
                )
            }
            order.sickLeave?.sickLeaveEndDate?.let { it1 ->
                binding.layoutLn.ivTo.setText(
                    it1.parseDate(
                        SimpleDateFormat("dd.MM.yyyy")
                    )
                )
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseDiagnosisInformation(it: ResponseOrder) {
        it.let { order ->
            binding.layoutDiagnosis.tvNamePatient.text = order.patient?.fIO
            if (!order.iCDDiagnosis?.name.isNullOrEmpty()) {
                binding.layoutDiagnosis.tvDiagnosis.text = order.iCDDiagnosis?.name
            }
            if (!order.iCDDiagnosis?.readmissionDate.isNullOrEmpty()) {
                order.iCDDiagnosis?.readmissionDate?.parseDate(
                    SimpleDateFormat("dd.MM.yyyy")
                )?.let { it1 -> binding.layoutDiagnosis.ivDate.setText(it1) }
            }
        }
    }

    private fun parsePhotoInformation(it: ResponseOrder) {
        it.let { order ->
            binding.layoutPhoto.tvNamePatient.text = order.patient?.fIO
            binding.layoutPhoto.etComment.setTextWithoutChangeEvents(it.commentPhoto)
        }
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun parsePaymentInformation(it: ResponseOrder) {
        it.let { order ->
            binding.layoutPayment.tvNamePatient.text = order.patient?.fIO
            binding.layoutPayment.etPassportDate.makeTextUntouchable()
            binding.layoutPayment.etPassportDate.clickAction {
                datePicker.show(parentFragmentManager, "PAYMENT")
                datePicker.addOnPositiveButtonClickListener {
                    viewModel.setPassportDate(it)
                    binding.layoutPayment.etPassportDate.setText(
                        SimpleDateFormat("dd.MM.yyyy").format(
                            Date(it)
                        )
                    )
                }
            }
            val listServices = viewModel.order.value?.medicalServices?.filter {
                it.paymentByThePatient == true
            }?.toMutableList()
            val totalAmount = listServices?.sumOf { it.price ?: 0 }
            binding.layoutPayment.tvAmount.text = " К оплате $totalAmount р."

            binding.layoutPayment.etPassport.setTextWithoutChangeEvents(it.patient?.passportNumber)
            binding.layoutPayment.etPassportDate.setTextWithoutChangeEvents(
                it.patient?.passportDateOfIssue?.parseDate(
                    SimpleDateFormat("dd.MM.yyyy")
                )
            )
            binding.layoutPayment.etPassportWho.setTextWithoutChangeEvents(it.patient?.passportIssuedBy)
            binding.layoutPayment.etPassportAddress.setTextWithoutChangeEvents(it.patient?.registrationAddress)
            binding.layoutPayment.etComment.setTextWithoutChangeEvents(it.commentPayment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun parseMedicalServicesInformation(order: ResponseOrder) {
        order.let { order ->
            binding.layoutMedicalServices.tvNamePatient.text = order.patient?.fIO
            servicesAdapter.items = viewModel.firstListMedicalServices
            servicesAdapter.notifyDataSetChanged()
            secondServicesAdapter.items = viewModel.secondListMedicalServices
            secondServicesAdapter.notifyDataSetChanged()
            updatePrice()
            if (!order.paymentGUID.isNullOrEmpty()) {
                binding.layoutMedicalServices.btnProceed.isEnabled = false
                binding.layoutMedicalServices.tvAddService.makeGone()
                binding.layoutMedicalServices.listDropDown.isEnabled = false
            } else {
                binding.layoutMedicalServices.listDropDown.makeVisible()
            }
            try {
                viewModel.laboratories.value?.indexOfFirst {
                    it.gUID == order.gUIDLaboratory
                }?.let { binding.layoutMedicalServices.listDropDown.selectItemByIndex(it) }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun parseOrderInformation(order: ResponseOrder) = with(binding.layoutOrderInformation) {
        val mapStatusToAction = mapOf(
            "Свободная" to "Выехать",
            "Принята" to "Выехать",
            "Врач выехал" to "Начать прием",
            "Начат прием" to "Завершить прием",
            "Завершен прием" to "Финализировать"
        )

        order.let { order ->
            tvNamePatient.text = order.patient?.fIO
            tvAddress.text = order.requestAdress
            tvBirthday.text =
                "Дата рождения: ${order.patient?.birthday?.parseDate(SimpleDateFormat("dd.MM.yyyy"))}"
            tvNumber.text = "Номер полиса: ${order.policyNumber}\nСК: ${order.inshuranceCompany}"
            tvNumberSk.text = makeLink(
                text = "Телефон СК: ${order.inshuranceCompanyPhone}",
                phrase = order.inshuranceCompanyPhone.toString(),
                listener = { "tel:" + order.inshuranceCompanyPhone?.callPhoneNumber(requireContext()) }
            )
            tvPhone.text = makeLink(
                text = "Телефон: ${order.phone}",
                phrase = order.phone.toString(),
                listener = { "tel:" + order.phone?.callPhoneNumber(requireContext()) }
            )
            tvPhone.setSafeOnClickListener {
                ("tel:" + order.phone).callPhoneNumber(requireContext())
            }
            tvNumberSk.setSafeOnClickListener {
                ("tel:" + order.inshuranceCompanyPhone).callPhoneNumber(requireContext())
            }
            tvDescription.text = order.complaints
            tvDescription.isVisible = !order.complaints.isNullOrEmpty()
            tvComments.text = order.comment
            tvComments.isVisible = !order.comment.isNullOrEmpty()

            when (order.status) {
                "Свободная" -> {
                    layoutButtonsFreeStatusOrder.makeVisible()
                    btnProceed.makeGone()
                }

                else -> {
                    layoutButtonsFreeStatusOrder.makeGone()
                    btnProceed.makeVisible()
                    btnProceed.text = mapStatusToAction[order.status]
                }
            }
            binding.scrollLayoutButtons.isVisible = order.status != "Свободная"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initClickers() {
        binding.rbMedicalServices.uncheckAndSetCheckedListener {
            binding.layoutOrderInformation.container.makeGone()
            binding.layoutPayment.container.makeGone()
            binding.layoutMedicalServices.container.makeVisible()
            binding.layoutPhoto.container.makeGone()
            binding.layoutDiagnosis.container.makeGone()
            binding.layoutInspection.container.makeGone()
            binding.layoutLn.container.makeGone()
            binding.ivSave.makeVisible()
            viewModel.order.value?.requestGUID?.let { viewModel.getOrder(it) }
        }
        binding.rbInformation.uncheckAndSetCheckedListener {
            binding.layoutOrderInformation.container.makeVisible()
            binding.layoutPayment.container.makeGone()
            binding.layoutMedicalServices.container.makeGone()
            binding.layoutPhoto.container.makeGone()
            binding.layoutDiagnosis.container.makeGone()
            binding.layoutInspection.container.makeGone()
            binding.layoutLn.container.makeGone()
            binding.ivSave.makeGone()
            viewModel.updateOrder(false)
        }
        binding.rbLn.uncheckAndSetCheckedListener {
            binding.layoutOrderInformation.container.makeGone()
            binding.layoutPayment.container.makeGone()
            binding.layoutMedicalServices.container.makeGone()
            binding.layoutPhoto.container.makeGone()
            binding.layoutDiagnosis.container.makeGone()
            binding.layoutInspection.container.makeGone()
            binding.layoutLn.container.makeVisible()
            binding.ivSave.makeVisible()
        }
        binding.rbPay.uncheckAndSetCheckedListener {
            binding.layoutOrderInformation.container.makeGone()
            binding.layoutMedicalServices.container.makeGone()
            binding.layoutPayment.container.makeVisible()
            binding.layoutPhoto.container.makeGone()
            binding.layoutDiagnosis.container.makeGone()
            binding.layoutInspection.container.makeGone()
            binding.layoutLn.container.makeGone()
            binding.ivSave.makeVisible()
            viewModel.updateOrder(false)
        }
        binding.rbDiagnosis.uncheckAndSetCheckedListener {
            binding.layoutOrderInformation.container.makeGone()
            binding.layoutMedicalServices.container.makeGone()
            binding.layoutPayment.container.makeGone()
            binding.layoutPhoto.container.makeGone()
            binding.layoutDiagnosis.container.makeVisible()
            binding.layoutInspection.container.makeGone()
            binding.layoutLn.container.makeGone()
            binding.ivSave.makeVisible()
        }
        binding.rbPhoto.uncheckAndSetCheckedListener {
            binding.layoutOrderInformation.container.makeGone()
            binding.layoutMedicalServices.container.makeGone()
            binding.layoutPayment.container.makeGone()
            binding.layoutPhoto.container.makeVisible()
            binding.layoutDiagnosis.container.makeGone()
            binding.layoutInspection.container.makeGone()
            binding.layoutLn.container.makeGone()
            binding.ivSave.makeVisible()
            viewModel.getFilesFromOrder()
        }
        binding.rbInspection.uncheckAndSetCheckedListener {
            binding.layoutOrderInformation.container.makeGone()
            binding.layoutMedicalServices.container.makeGone()
            binding.layoutDiagnosis.container.makeGone()
            binding.layoutPayment.container.makeGone()
            binding.layoutPhoto.container.makeGone()
            binding.layoutInspection.container.makeVisible()
            binding.layoutLn.container.makeGone()
            binding.ivSave.makeVisible()
        }
        binding.rbInformation.isChecked = true
        binding.layoutOrderInformation.btnProceed.setSafeOnClickListener {
            viewModel.changeStatus()
        }
        binding.layoutOrderInformation.btnAccept.setSafeOnClickListener {
            viewModel.acceptOrder()
        }
        binding.layoutOrderInformation.btnReject.setSafeOnClickListener {
            viewModel.rejectOrder()
        }
        binding.layoutMedicalServices.tvAddService.setSafeOnClickListener {
            viewModel.selectedLaboratory?.gUID?.let { it1 ->
                viewModel.getListServices(
                    laboratoryId = it1
                )
            }
        }
        binding.layoutMedicalServices.listDropDown.setOnSpinnerItemSelectedListener<String> { _, oldItem, newIndex, newItem ->
            if (newItem != oldItem) {
                viewModel.getSelectedLaboratory(
                    index = newIndex
                )
            }
        }
        binding.layoutLn.listDropDown.setOnSpinnerItemSelectedListener<String> { _, oldItem, newIndex, newItem ->
            if (newItem != oldItem) {
                viewModel.selectStatusSick(
                    index = newIndex
                )
            }
        }
        binding.layoutMedicalServices.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.button1 -> {
                    if (isChecked) {
                        selectedFirstButton = true
                        selectedSecondButton = false
                        binding.layoutMedicalServices.btnProceed.makeGone()
                        binding.layoutMedicalServices.rvMedicalServices.makeVisible()
                        binding.layoutMedicalServices.rvSecondMedicalServices.makeGone()
                    }
                }

                R.id.button2 -> {
                    if (isChecked) {
                        selectedFirstButton = false
                        selectedSecondButton = true
                        updatePrice()
                        binding.layoutMedicalServices.rvMedicalServices.makeGone()
                        binding.layoutMedicalServices.rvSecondMedicalServices.makeVisible()
                    }
                }
            }
        }
        binding.ivSave.setSafeOnClickListener {
            viewModel.updateOrder(false)
        }
        binding.layoutMedicalServices.btnProceed.setSafeOnClickListener {
            viewModel.updateOrder(true)
        }
        binding.layoutPayment.etComment.doAfterTextChanged {
            viewModel.setComment(it)
        }
        binding.layoutPayment.etPassport.doAfterTextChanged {
            viewModel.setPassport(it)
        }
        binding.layoutPayment.etPassportWho.doAfterTextChanged {
            viewModel.setPassportWho(it)
        }
        binding.layoutPayment.etPassportAddress.doAfterTextChanged {
            viewModel.setPassportAddress(it)
        }
        binding.layoutPayment.btnLink.setSafeOnClickListener {
            checkAndShowErrorsIfFieldsEmpty("LINK")
        }
        binding.layoutPayment.btnQr.setSafeOnClickListener {
            checkAndShowErrorsIfFieldsEmpty("QR")
        }
        binding.layoutPayment.tvLink.setSafeOnClickListener {
            if (!binding.layoutPayment.tvLink.text.isNullOrEmpty()) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(binding.layoutPayment.tvLink.text.toString())
                startActivity(i)
            }
        }
        binding.layoutPayment.btnLinkUpdateStatus.setSafeOnClickListener {
            viewModel.generatePayment(type = "LINK")
        }
        binding.layoutPayment.btnQrUpdateStatus.setSafeOnClickListener {
            viewModel.generatePayment(type = "QR")
        }
        binding.layoutPhoto.etComment.doAfterTextChanged {
            viewModel.setCommentPhoto(it)
        }
        binding.layoutDiagnosis.ivDate.clickAction {
            datePicker.show(parentFragmentManager, "DIAGNOSIS")
            datePicker.addOnPositiveButtonClickListener {
                viewModel.setDiagnosisDate(it.parseDateToServer())
                binding.layoutDiagnosis.ivDate.setText(
                    SimpleDateFormat("dd.MM.yyyy").format(
                        Date(it)
                    )
                )
            }
        }
        binding.layoutDiagnosis.etDiagnosis.setSafeOnClickListener {
            val dialogListDiagnosis = DialogListDiagnosis()
            dialogListDiagnosis.setCallback(object : DialogListDiagnosis.Callback {
                override fun onItemSelected(item: ResponseDiagnosis) {
                    viewModel.setDiagnosis(item)
                    binding.layoutDiagnosis.tvDiagnosis.text = item.name
                }
            })
            dialogListDiagnosis.show(parentFragmentManager, DialogListDiagnosis.TAG)
        }
        binding.layoutLn.ivFrom.setSafeOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату")
                .build()
            datePicker.show(parentFragmentManager, "FROM_DATE")
            datePicker.addOnPositiveButtonClickListener {
                viewModel.setFromDateLn(it.parseDateToServer())
            }
        }
        binding.layoutLn.ivTo.setSafeOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату")
                .build()
            datePicker.show(parentFragmentManager, "TO_DATE")
            datePicker.addOnPositiveButtonClickListener {
                viewModel.setToDateLn(it.parseDateToServer())
            }
        }
        binding.layoutLn.etSnils.doAfterTextChanged {
            viewModel.setSnils(it)
        }
        binding.layoutLn.etValue.doAfterTextChanged {
            viewModel.setCommentLn(it)
        }
        binding.layoutLn.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                viewModel.setSickLeaveNotRequired(isChecked)
            }
        }
        binding.layoutLn.listDropDown.apply {
            this.lifecycleOwner = viewLifecycleOwner
            this.setItems(viewModel.getStatusesLnList())
            this.arrowGravity = SpinnerGravity.END
            this.arrowTint =
                ContextCompat.getColor(requireContext(), ru.soft.ui_atoms.R.color.black)
            this.spinnerPopupAnimation = SpinnerAnimation.NORMAL
            this.showDivider = true
            this.gravity = Gravity.CENTER_VERTICAL
            this.dividerColor = Color.WHITE
            this.dividerSize = 2
            this.preferenceName = "sdfgsdg"
        }
    }

    private fun checkAndShowErrorsIfFieldsEmpty(type: String) {
        val patient = viewModel.order.value?.patient

        if (patient?.registrationAddress.isNullOrEmpty()) {
            binding.layoutPayment.etPassportAddress.setAndShowError("Заполните поле")
        } else if (patient?.passportNumber.isNullOrEmpty()) {
            binding.layoutPayment.etPassport.setAndShowError("Заполните поле")
        } else if (patient?.passportIssuedBy.isNullOrEmpty()) {
            binding.layoutPayment.etPassportWho.setAndShowError("Заполните поле")
        } else if (patient?.passportDateOfIssue.isNullOrEmpty()) {
            binding.layoutPayment.etPassportDate.setAndShowError("Заполните поле")
        } else {
            viewModel.generatePayment(type = type)
        }
    }


    private fun initViews() {
        binding.layoutMedicalServices.rvMedicalServices.makeVisible()
        binding.layoutMedicalServices.rvMedicalServices.adapter = servicesAdapter
        binding.layoutMedicalServices.rvSecondMedicalServices.adapter = secondServicesAdapter
        binding.layoutPhoto.rvItems.adapter = filesAdapter
        binding.layoutInspection.rvItems.adapter = inspectionAdapter
    }

    private fun HorizontalListCheckItemView.uncheckAndSetCheckedListener(action: () -> Unit) {
        isChecked = false
        setDefaultCheckChangeListener { if (it) action() }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePrice() {
        binding.layoutMedicalServices.btnProceed.isVisible =
            viewModel.secondListMedicalServices.isNotEmpty() &&
                    binding.layoutMedicalServices.button2.isChecked
        binding.layoutMedicalServices.btnProceed.text =
            "К оплате " + (viewModel.secondListMedicalServices.sumOf {
                it.price ?: 0
            }).toString() + " р"
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile(
            TMP_FILE_NAME,
            TMP_FILE_FORMAT
        ).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${PACKAGE}${FILE_PROVIDER_AUTHORITY}",
            tmpFile
        )
    }

    private fun launchWithPermission(permission: String, grantedAction: () -> Unit) {
        Dexter.withContext(requireContext())
            .withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    grantedAction()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    request: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            70,
            outputStream
        ) // Adjust compression quality as needed
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return null
    }

    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()

        val newWidth: Int
        val newHeight: Int

        if (aspectRatio > 1) {
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun pickFiles(it: ResponseListTypeFiles) {
        selectedTypeFile = it
        if (!it.isHaveFile) {
            val dialog = DialogPickImageFragment()
            dialog.setCallback(object : DialogPickImageFragment.Callback {
                override fun openCamera() {
                    launchWithPermission(Manifest.permission.CAMERA) {
                        lifecycleScope.launch {
                            getTmpFileUri().let { uri ->
                                latestTmpUri = uri
                                cameraLauncher.launch(uri)
                            }
                        }
                    }
                }

                override fun openGallery() {
                    pickImagesLauncher.launch(IMAGE_CONTENT_TYPE)
                }
            })
            dialog.show(parentFragmentManager, DialogPickImageFragment.TAG)
        }
    }

}