package com.example.bettinalogistics.ui.addorder

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.AddNewProductBottomSheetDialogBinding
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddNewProductBottomSheet : BottomSheetDialogFragment() {
    var mButtonAddListener: ((Uri?, String?, String?, String?, String?, String?, String?, Boolean) -> Unit)? =
        null

    private var uri: Uri? = null
    private var isLCL :Boolean = false

    private val binding: AddNewProductBottomSheetDialogBinding by lazy {
        AddNewProductBottomSheetDialogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyleBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        initListener()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            Handler().postDelayed(Runnable {
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                setupFullHeight(bottomSheetDialog)
            }, 200L)
        }

        return dialog
    }

    private fun initView() {
        binding.btnAddOrderNewProductLCL.setBackgroundResource(R.drawable.custom_bg_secondary_sea_green_button_corner_20)
    }

    private fun initListener() {
        binding.ivAttachmentNewImageProduct.setOnClickListener {
            pickImage()
        }
        binding.btnAddOrderNewProductLCL.setOnClickListener {
            isLCL = true
            setViewWhenClickLCL()
        }
        binding.btnAddOrderNewProductFCL.setOnClickListener {
            isLCL = false
            setViewWhenClickFcl()
        }

        binding.btnAddNewProduct.setOnClickListener {
            val name = binding.edtAddNewProductName.text.toString()
            val des = binding.edtAddNewProductDescription.text.toString()
            val quantity = binding.edtAddNewProductQuantity.text.toString()
            val volume = binding.edtAddNewProductVolume.text.toString()
            val mass = binding.edtAddNewProductMass.text.toString()
            val numberOfCarton = binding.edtAddNewProductNumberOfCarton.text.toString()
            mButtonAddListener?.invoke(uri, name, des, quantity, volume, mass, numberOfCarton, isLCL)
        }
    }

    private fun setViewWhenClickFcl() {
        binding.btnAddOrderNewProductFCL.setBackgroundResource(R.drawable.custom_bg_secondary_sea_green_button_corner_20)
        binding.btnAddOrderNewProductFCL.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        binding.btnAddOrderNewProductLCL.setBackgroundResource(R.drawable.bg_green_corne_20_width_1_5)
        binding.btnAddOrderNewProductLCL.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.ivExpanseQuantityForFlc.visibility = View.VISIBLE
        binding.tvAddNewProductVolumeTitle.visibility = View.GONE
        binding.edtAddNewProductVolume.visibility = View.GONE
        binding.tvAddNewProductMassTitle.visibility = View.GONE
        binding.edtAddNewProductMass.visibility = View.GONE
        binding.tvAddNewProductNumberOfCartonTitle.visibility = View.GONE
        binding.edtAddNewProductNumberOfCarton.visibility = View.GONE
        binding.tvUriNewImageProduct.text = ""
        binding.edtAddNewProductName.text?.clear()
        binding.edtAddNewProductDescription.text?.clear()
        binding.edtAddNewProductQuantity.text?.clear()
    }

    private fun setViewWhenClickLCL() {
        binding.btnAddOrderNewProductLCL.setBackgroundResource(R.drawable.custom_bg_secondary_sea_green_button_corner_20)
        binding.btnAddOrderNewProductLCL.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
        binding.btnAddOrderNewProductFCL.setBackgroundResource(R.drawable.bg_green_corne_20_width_1_5)
        binding.btnAddOrderNewProductFCL.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.ivExpanseQuantityForFlc.visibility = View.GONE
        binding.tvAddNewProductVolumeTitle.visibility = View.VISIBLE
        binding.edtAddNewProductVolume.visibility = View.VISIBLE
        binding.tvAddNewProductMassTitle.visibility = View.VISIBLE
        binding.edtAddNewProductMass.visibility = View.VISIBLE
        binding.tvAddNewProductNumberOfCartonTitle.visibility = View.VISIBLE
        binding.edtAddNewProductNumberOfCarton.visibility = View.VISIBLE
        binding.tvUriNewImageProduct.text = ""
        binding.edtAddNewProductName.text?.clear()
        binding.edtAddNewProductDescription.text?.clear()
        binding.edtAddNewProductQuantity.text?.clear()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK
            && result.data != null
        ) {
            uri = result.data!!.data
            binding.tvAddNewProductImageTitle.text = Utils.g().getFullNameShort(uri.toString())
        } else {
            Log.d(TAG, "result = null: ")
        }
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = binding.root.height
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}