package ru.soft.feature_home.presentation.order

import android.annotation.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.view.*
import androidx.fragment.app.*
import com.google.android.material.bottomsheet.*
import kotlinx.coroutines.flow.*
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_home.data.models.*
import ru.soft.feature_home.databinding.*
import ru.soft.feature_home.di.*
import ru.soft.feature_home.presentation.*
import javax.inject.*

class DialogListDiagnosis : BottomSheetDialogFragment() {

    interface Callback {
        fun onItemSelected(item: ResponseDiagnosis)
    }

    @Inject
    lateinit var viewModelFactory: OrderViewModelFactory
    private val viewModel by viewModels<OrderViewModel> {
        viewModelFactory
    }

    private val diagnosisAdapter by lazy {
        MultiBindingAdapter(
            diagnosis(
                onClick = {
                    callback?.onItemSelected(it)
                    dismiss()
                }
            )
        )
    }

    companion object {

        const val TAG = "DialogListDiagnosis"

    }

    private lateinit var binding: DialogListMedicalServicesBinding
    private var callback: Callback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeComponent.create((requireActivity().application as AppWithFacade).getFacade())
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
        }
        binding = DialogListMedicalServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = diagnosisAdapter

        binding.searchEditText.doAfterTextChanged {
            if (it.length >= 3) {
                viewModel.searchDiagnosis(
                    it
                )
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.searchDiagnosis.collect {
                if (it.isNotEmpty()) {
                    diagnosisAdapter.items = it
                }
            }
        }

    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }
}