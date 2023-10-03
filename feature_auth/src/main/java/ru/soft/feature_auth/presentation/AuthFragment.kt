package ru.soft.feature_auth.presentation

import android.os.*
import android.view.*
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
import javax.inject.*

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val binding by viewBinding(FragmentAuthBinding::bind)

    @Inject
    lateinit var viewModelFactory: AuthViewModelFactory
    private val viewModel by viewModels<AuthViewModel> { viewModelFactory }

    @Inject
    lateinit var router: Router

    companion object {
        fun newInstance() = AuthFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthComponent.create((requireActivity().application as AppWithFacade).getFacade())
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivPhone.setHint("Телефон +7 (ХХХ) ХХХ-ХХ-ХХ")
        binding.ivPhone.invalidate()
        binding.btnSendCode.setOnClickListener {
            viewModel.login(binding.ivPhone.getText())
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.isSuccessSent.collect {
                if (it) {
                    router.replaceScreen(object : FragmentScreen {
                        override fun createFragment(factory: FragmentFactory): Fragment {
                            return SmsCodeFragment.newInstance(
                                binding.ivPhone.getText()
                            )
                        }
                    })
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.isErrorSent.collect {
                if (it.isNotEmpty()) {
                    binding.ivPhone.setAndShowError(it)
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.loading.collect {
                binding.layoutProgress.layoutProgress.isVisible = it
            }
        }
    }
}

