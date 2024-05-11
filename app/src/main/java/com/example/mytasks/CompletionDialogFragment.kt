package com.example.mytasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment


class CompletionDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_completed_theme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find and initialize the close button
        val closeButtonCom: Button = view.findViewById(R.id.closeButtonCom)

        // Set up click listener for the close button
        closeButtonCom.setOnClickListener {
            dismiss() // Dismiss the dialog fragment when the button is clicked
        }
    }

    companion object {
        fun newInstance(): CompletionDialogFragment {
            return CompletionDialogFragment()
        }
    }
}
