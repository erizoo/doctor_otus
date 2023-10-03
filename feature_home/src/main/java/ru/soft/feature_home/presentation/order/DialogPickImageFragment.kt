package ru.soft.feature_home.presentation.order

import android.annotation.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.view.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.feature_home.data.models.*
import ru.soft.feature_home.databinding.*
import ru.soft.feature_home.presentation.*

class DialogPickImageFragment : BottomSheetDialogFragment() {

    interface Callback {

        fun openCamera()

        fun openGallery()

    }

    companion object {
        const val TAG = "DialogPickImageFragment"
    }

    private lateinit var binding: DialogPickImageFragmentBinding
    private var callback: Callback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
        }
        binding = DialogPickImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCamera.setSafeOnClickListener {
            callback?.openCamera()
            dismiss()
        }
        binding.tvGallery.setSafeOnClickListener {
            callback?.openGallery()
            dismiss()
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

}