package ru.soft.feature_auth.presentation

import android.*
import android.annotation.*
import android.content.*
import android.content.pm.*
import android.os.*
import android.text.*
import android.view.*
import androidx.activity.*
import androidx.activity.result.contract.*
import androidx.core.content.*
import androidx.core.view.*
import androidx.fragment.app.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.utils.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_auth.R
import ru.soft.feature_auth.databinding.*
import ru.soft.feature_auth.di.*
import ru.soft.feature_home.*
import ru.soft.main_api.*
import java.text.*
import java.util.*
import javax.inject.*

class SmsCodeFragment : Fragment(R.layout.fragment_sms_code) {

    private val binding by viewBinding(FragmentSmsCodeBinding::bind)
    private val phoneNumber: String? by argument(ARG_PHONE_NUMBER, null)

    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory
    private val viewModel by viewModels<AuthViewModel> { viewModelFactory }

    @Inject
    lateinit var homeMediator: HomeMediator

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private var millisRemaining: Long = 120000
    private var countDownTimer: CountDownTimer? = null

    companion object {
        const val ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER"

        fun newInstance(
            phoneNumber: String
        ): SmsCodeFragment {
            val fragment = SmsCodeFragment()
            val args = Bundle()
            args.putString(ARG_PHONE_NUMBER, phoneNumber)
            fragment.arguments = args
            return fragment
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { _: Boolean ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthComponent.create((requireActivity().application as AppWithFacade).getFacade())
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initClickers()
        initObservers()
        askNotificationPermission()
        startTimer()

    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                println("")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun initClickers() {
        binding.btnSendCode.setOnClickListener {
            binding.ivSmsCode.hideError()
            phoneNumber?.let { phone ->
                viewModel.checkSmsCode(
                    phone = phone,
                    code = binding.ivSmsCode.getText()
                )
            }
        }
        binding.tvSms.setOnClickListener {
            phoneNumber?.let { phoneNumber -> viewModel.login(phoneNumber) }
        }
        binding.ivSmsCode.doAfterTextChanged {
            binding.ivSmsCode.hideError()
        }
    }

    private fun initObservers() {
        requireActivity().onBackPressedDispatcher.addCallback {
            router.replaceScreen(object : FragmentScreen {
                override fun createFragment(factory: FragmentFactory): Fragment {
                    return AuthFragment.newInstance()
                }
            })
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.isSuccessSent.collect {
                if (it) {
                    startTimer()
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.isSuccessCheckedCode.collect {
                if (it) {
                    homeMediator.openHomeScreen()
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.isErrorSent.collect {
                if (it.isNotEmpty()) {
                    binding.ivSmsCode.setAndShowError(it)
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.loading.collect {
                binding.layoutProgress.layoutProgress.isVisible = it
            }
        }
    }

    private fun initViews() {
        binding.ivSmsCode.setHint("Введите код из смс")
        binding.ivSmsCode.setInputType(
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    or InputType.TYPE_NUMBER_FLAG_SIGNED
        )
    }

    private fun startTimer() {
        createTimer(millisRemaining)
        countDownTimer?.start()
        binding.tvSms.isClickable = false
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
    }

    private fun createTimer(millisUntilFinished: Long) {
        countDownTimer = object : CountDownTimer(millisUntilFinished, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                val date = Date(millisUntilFinished)
                binding.tvSms.text =
                    "Повторная отправка СМС доступна через ${dateFormat.format(date)}"
            }

            override fun onFinish() {
                binding.tvSms.text = "Отправить повторно"
                binding.tvSms.isClickable = true
            }
        }
    }
}
