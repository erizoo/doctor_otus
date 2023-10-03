package ru.soft.base_arch.base

import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.*
import androidx.annotation.*
import androidx.fragment.app.*
import com.google.android.material.bottomsheet.*


abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private val backPressedDispatcher = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            (this@BaseFragment as IBackPressed).onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this is IBackPressed) requireActivity().apply {
            onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedDispatcher)
        }
    }

    fun showDialogError(errorString : String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(ru.soft.base_arch.R.layout.error_bottom_sheet_dialog)

        val text = bottomSheetDialog.findViewById<TextView>(ru.soft.base_arch.R.id.tv_text)
        text?.text = errorString

        bottomSheetDialog.show()
    }

}