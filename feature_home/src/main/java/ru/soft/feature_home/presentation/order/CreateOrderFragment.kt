package ru.soft.feature_home.presentation.order

import android.graphics.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.core.content.*
import androidx.fragment.app.*
import com.skydoves.powerspinner.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.utils.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_home.R
import ru.soft.feature_home.databinding.*
import ru.soft.feature_home.di.*
import ru.soft.main_api.*
import javax.inject.*

class CreateOrderFragment : BaseFragment(R.layout.fragment_create_order) {

    private val binding by viewBinding(FragmentCreateOrderBinding::bind)

    @Inject
    lateinit var viewModelFactory: OrderViewModelFactory
    private val viewModel by viewModels<OrderViewModel> {
        viewModelFactory
    }

    companion object {

        const val TAG = "CreateOrderFragment"

    }

    @Inject
    lateinit var mainMediator: MainMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeComponent.create((requireActivity().application as AppWithFacade).getFacade())
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getListInsuranceCompanies()

        binding.btnAccept.setSafeOnClickListener {
            viewModel.createOrder(
                fio = binding.etFio.getText(),
                phone = binding.etPhone.getText(),
                email = binding.etEmail.getText(),
                complaints = binding.etValue.getText(),
                insurancePolicyNumber = binding.etNumber.getText(),
                comment = binding.etComment.getText()
            )
        }

        binding.listDropDown.setOnSpinnerItemSelectedListener<String> { _, oldItem, newIndex, newItem ->
            if (newItem != oldItem) {
                viewModel.selectedInsuranceCompany =
                    viewModel.successInsuranceCompanies.value?.get(newIndex)
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.successCreateOrder.collect {
                if (!it.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    mainMediator.openHome(requireContext())
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.successInsuranceCompanies.collect {
                if (it?.isNotEmpty() == true) {
                    binding.listDropDown.apply {
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

        binding.etFio.doAfterTextChanged {
            checkValidate()
        }
        binding.etNumber.doAfterTextChanged {
            checkValidate()
        }
        binding.etValue.doAfterTextChanged {
            checkValidate()
        }
    }

    private fun checkValidate() {
        binding.btnAccept.isEnabled =
            binding.etFio.getText().isNotEmpty() && binding.etNumber.getText()
                .isNotEmpty() && binding.etValue.getText().isNotEmpty()
    }
}