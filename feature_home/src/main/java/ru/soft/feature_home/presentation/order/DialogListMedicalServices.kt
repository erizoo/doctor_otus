package ru.soft.feature_home.presentation.order

import android.annotation.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.view.*
import com.google.android.material.bottomsheet.*
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.data.models.*
import ru.soft.feature_home.databinding.*
import ru.soft.feature_home.presentation.*

class DialogListMedicalServices : BottomSheetDialogFragment() {

    interface Callback {
        fun onItemSelected(item: ResponseMedicalService)
    }

    private val orderAdapter by lazy {
        MultiBindingAdapter(
            medicalServices(
                onClick = {
                    callback?.onItemSelected(it)
                    dismiss()
                }
            )
        )
    }

    companion object {

        const val ARG_ITEMS = "ARG_ITEMS"
        const val TAG = "DialogListMedicalServices"

        fun newInstance(
            services: List<ResponseMedicalService>
        ): DialogListMedicalServices {
            val fragment = DialogListMedicalServices()
            val args = Bundle()
            args.putParcelableArrayList(ARG_ITEMS, ArrayList(services))
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var binding: DialogListMedicalServicesBinding

    private var callback: Callback? = null
    private var originalList: List<ResponseMedicalService>? by argument(ARG_ITEMS, null)
    private var filteredList = mutableListOf<ResponseMedicalService>()

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

        filteredList.clear()
        originalList?.let { filteredList.addAll(it) }

        binding.searchEditText.doAfterTextChanged { text ->
            filteredList.clear()
            if (text.isNotEmpty()) {
                filteredList.addAll(originalList?.filter { item ->
                    item.name?.contains(text, ignoreCase = true) == true
                } ?: emptyList())
            } else {
                filteredList.addAll(originalList ?: emptyList())

            }
            orderAdapter.notifyDataSetChanged()
        }

        orderAdapter.items = filteredList

        binding.recyclerView.adapter = orderAdapter
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }
}