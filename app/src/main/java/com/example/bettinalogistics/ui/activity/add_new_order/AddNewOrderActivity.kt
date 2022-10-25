package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityAddNewOrderBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddNewOrderActivity : BaseActivity() {

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
        observerData()
    }

    private fun initView() {
        binding.btnAddOrderNewProductLCL.setBackgroundResource(R.drawable.custom_bg_secondary_sea_green_button_corner_20)
    }

    private fun initListener() {
        binding.ivAttachmentNewImageProduct.setOnClickListener {
            pickImage()
        }
        binding.btnAddOrderNewProductLCL.setOnClickListener {
            viewModel.isLCL = true
            setViewWhenClickLCL()
        }
        binding.btnAddOrderNewProductFCL.setOnClickListener {
            viewModel.isLCL = false
            setViewWhenClickFcl()
        }

        binding.btnAddNewProduct.setOnClickListener {
            val name = binding.edtAddNewProductName.text.toString()
            val des = binding.edtAddNewProductDescription.text.toString()
            val quantity = binding.edtAddNewProductQuantity.text.toString()
            val volume = binding.edtAddNewProductVolume.text.toString()
            val mass = binding.edtAddNewProductMass.text.toString()
            val numberOfCarton = binding.edtAddNewProductNumberOfCarton.text.toString()
            showLoading()
            val order =
                Order(
                    imgUri = viewModel.uri,
                    productName = name,
                    productDes = des,
                    quantity = quantity.toLong(),
                    volume = volume.toDouble(),
                    mass = mass.toDouble(),
                    numberOfCarton = numberOfCarton.toLong(),
                    isOrderLCL = viewModel.isLCL,
                    status = DataConstant.PRODUCT_STATUS_PENDING,
                    orderDate = Calendar.getInstance().time
                )
            if (viewModel.checkInvalidData(order, this)) {
                viewModel.addOrder(order)
            }
        }
    }

    private fun observerData() {
        viewModel.checkValidDataOrderLiveData.observe(this) {
            confirm.newBuild().setNotice(it)
        }
        viewModel.addOrderLiveData.observe(this) {
            if (it) {
                confirm.newBuild().addButtonAgree(getString(R.string.str_add_order_success)) {
                    hiddenLoading()
                    finish()
                }
            } else {
                confirm.newBuild().addButtonAgree(getString(R.string.str_add_order_fail)) {
                    hiddenLoading()
                    finish()
                }
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

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK
            && result.data != null
        ) {
            viewModel.uri = result.data!!.data
            binding.tvUriNewImageProduct.text = viewModel.uri.toString().substring(0,15)+"..."
        } else {
            Log.d(AppConstant.TAG, "result = null: ")
        }
    }
}