package com.example.bettinalogistics.ui.activity.add_new_order

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityAddNewOrderBinding
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNewOrderActivity : BaseActivity() {
    companion object {
        const val ADD_NEW_PRODUCT = "new_product"
    }

    override val layoutId: Int
        get() = R.layout.activity_add_new_order
    override val viewModel: AddNewOrderViewModel by viewModel()

    override val binding: ActivityAddNewOrderBinding by lazy {
        ActivityAddNewOrderBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        binding.btnAddOrderNewProductLCL.setBackgroundResource(R.drawable.custom_bg_secondary_sea_green_button_corner_20)
        binding.headerAddNewOrder.tvHeaderTitle.text = getString(R.string.str_add_new_product)
    }

    private fun initListener() {
        binding.ivAttachmentNewImageProduct.setOnClickListener {
            showPhotoPermission()
        }
        binding.btnAddOrderNewProductLCL.setOnClickListener {
            viewModel.isLCL = true
            setViewWhenClickLCL()
        }
        binding.btnAddOrderNewProductFCL.setOnClickListener {
            viewModel.isLCL = false
            setViewWhenClickFcl()
        }

        binding.headerAddNewOrder.ivHeaderBack.setOnClickListener {
            if (binding.edtAddNewProductName.text.isNullOrEmpty()
                && binding.edtAddNewProductDescription.text.isNullOrEmpty()
                && binding.edtAddNewProductQuantity.text.isNullOrEmpty()
                && binding.edtAddNewProductVolume.text.isNullOrEmpty()
                && binding.edtAddNewProductMass.text.isNullOrEmpty()
                && binding.edtAddNewProductNumberOfCarton.text.isNullOrEmpty()
                && binding.tvUriNewImageProduct.text.isNullOrEmpty()
            ) {
                finish()
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_confirm_out))
                    .addButtonAgree {
                        confirm.dismiss()
                    }.addButtonCancel(getString(com.example.baseapp.R.string.cancel)) {
                        confirm.dismiss()
                        finish()
                    }
            }
        }

        binding.btnAddNewProduct.setOnClickListener {
            val name = binding.edtAddNewProductName.text.toString()
            val des = binding.edtAddNewProductDescription.text.toString()
            val quantity = binding.edtAddNewProductQuantity.text.toString()
            var volume = 0.0
            var mass = 0.0
            var numberOfCarton = 0L
            if (viewModel.isLCL) {
                volume = binding.edtAddNewProductVolume.text.toString().toDouble()
                mass = binding.edtAddNewProductMass.text.toString().toDouble()
                numberOfCarton = binding.edtAddNewProductNumberOfCarton.text.toString().toLong()
            }
            val product =
                Product(
                    imgUri = viewModel.uri,
                    productName = name,
                    productDes = des,
                    quantity = quantity.toLong(),
                    volume = volume.toDouble(),
                    mass = mass.toDouble(),
                    numberOfCarton = numberOfCarton.toLong(),
                    isOrderLCL = viewModel.isLCL,
                )
            if (viewModel.checkInvalidData(product, this)) {
                val i = Intent()
                i.putExtra(ADD_NEW_PRODUCT, Utils.g().getJsonFromObject(product))
                setResult(RESULT_OK, i)
                finish()
            }
        }
    }

    private fun setViewWhenClickFcl() {
        binding.btnAddOrderNewProductFCL.setBackgroundResource(R.drawable.custom_bg_secondary_sea_green_button_corner_20)
        binding.btnAddOrderNewProductFCL.setTextColor(
            ContextCompat.getColor(
                this,
                com.example.baseapp.R.color.white
            )
        )
        binding.btnAddOrderNewProductLCL.setBackgroundResource(R.drawable.bg_green_corne_20_width_1_5)
        binding.btnAddOrderNewProductLCL.setTextColor(
            ContextCompat.getColor(
                this,
                com.example.baseapp.R.color.black
            )
        )
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
        binding.btnAddOrderNewProductLCL.setTextColor(
            ContextCompat.getColor(
                this,
                com.example.baseapp.R.color.white
            )
        )
        binding.btnAddOrderNewProductFCL.setBackgroundResource(R.drawable.bg_green_corne_20_width_1_5)
        binding.btnAddOrderNewProductFCL.setTextColor(
            ContextCompat.getColor(
                this,
                com.example.baseapp.R.color.black
            )
        )
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

    private fun showPhotoPermission() {
        val p2 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if ( p2 != PackageManager.PERMISSION_GRANTED) {
            requestPhotoPermission()
        } else {
            pickImage()
        }
    }

    private fun requestPhotoPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            123
        )
    }


    private fun pickImage() {
        // gơ app di chay lai
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK
            && result.data != null
        ) {
            val uri = result.data?.data!!
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.uri = uri.toString()
            binding.tvUriNewImageProduct.text = viewModel.uri.toString().substring(0, 15) + "..."
        } else {
            Log.d(AppConstant.TAG, "result = null: ")
        }
    }
}