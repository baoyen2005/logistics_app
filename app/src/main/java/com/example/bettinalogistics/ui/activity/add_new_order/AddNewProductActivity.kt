package com.example.bettinalogistics.ui.activity.add_new_order

import android.annotation.SuppressLint
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityAddNewOrderBinding
import com.example.bettinalogistics.di.AppData
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.ui.fragment.bottom_sheet.DialogCommonChooseOneItem
import com.example.bettinalogistics.utils.AppConstant
import com.example.bettinalogistics.utils.AppPermissionsUtils
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNewProductActivity : BaseActivity() {
    companion object {
        const val ADD_NEW_PRODUCT = "new_product"
        const val IS_EDIT = "is_edit"
        const val PRODUCT_EDIT = "product_edit"
    }

    private lateinit var appPermissions: AppPermissionsUtils
    override val viewModel: AddNewProductViewModel by viewModel()

    override val binding: ActivityAddNewOrderBinding by lazy {
        ActivityAddNewOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.btnAddOrderNewProductLCL.setBackgroundResource(R.drawable.custom_bg_secondary_sea_green_button_corner_20)
        binding.headerAddNewOrder.tvHeaderTitle.text = getString(R.string.str_add_new_product)
        binding.edtAddNewProductQuantity.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.edtAddNewProductVolume.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.edtAddNewProductMass.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.edtAddNewProductNumberOfCarton.setInputType(InputType.TYPE_CLASS_NUMBER)
        val isEdit = intent.getBooleanExtra(IS_EDIT, false)
        viewModel.isEdit = isEdit
        if (isEdit) {
            val product =
                intent.getStringExtra(PRODUCT_EDIT)
                    ?.let { Utils.g().getObjectFromJson(it, Product::class.java) }
            viewModel.editProduct = product
            binding.tvUriNewImageProduct.setValueContent(product?.imgUri ?: "")
            binding.edtAddNewProductDescription.setValueContent(product?.productDes ?: "")
            binding.edtAddNewProductName.setValueContent(product?.productName ?: "")
            binding.edtAddNewProductQuantity.setValueContent((product?.quantity ?: 0).toString())
            binding.tvAddOrderTypeCont.setValueContent(product?.contType ?: "")
            binding.edtAddNewProductVolume.setValueContent((product?.volume ?: 0).toString())
            binding.edtAddNewProductMass.setValueContent((product?.mass ?: 0.0).toString())
            binding.edtAddNewProductNumberOfCarton.setValueContent((product?.numberOfCarton ?: 0).toString())
            binding.gTvNewProductType.setValueContent(product?.type ?: "0")
            binding.btnAddNewProduct.text = getString(R.string.str_update)
        }
        else{
            binding.tvUriNewImageProduct.clearContent()
            binding.edtAddNewProductDescription.clearContent()
            binding.edtAddNewProductName.clearContent()
            binding.edtAddNewProductQuantity.clearContent()
            binding.tvAddOrderTypeCont.clearContent()
            binding.edtAddNewProductVolume.clearContent()
            binding.edtAddNewProductMass.clearContent()
            binding.edtAddNewProductNumberOfCarton.clearContent()
            binding.gTvNewProductType.clearContent()
            binding.btnAddNewProduct.text = getString(R.string.str_create_product)
        }
        appPermissions = AppPermissionsUtils()
        if (!appPermissions.isStorageOk(applicationContext)) {
            appPermissions.requestStoragePermission(this)
        }
    }

    override fun initListener() {

        binding.tvUriNewImageProduct.setOnClickListener {
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

        binding.gTvNewProductType.setBackgroundClickListener {
            val chooseOneItem = DialogCommonChooseOneItem(viewModel.getListProductType()) {
                binding.gTvNewProductType.setValueContent(it.title)
                if (!AppData.g().productTypeSelected.contains(it)) {
                    AppData.g().productTypeSelected.add(it)
                }
            }
            chooseOneItem.show(supportFragmentManager, "aaaaaa")
        }

        binding.tvAddOrderTypeCont.setBackgroundClickListener {
            val chooseOneItem = DialogCommonChooseOneItem(viewModel.getListContType()) {
                binding.tvAddOrderTypeCont.setValueContent(it.title)
                if (!AppData.g().typeContSelected.contains(it)) {
                    AppData.g().typeContSelected.add(it)
                }
            }
            chooseOneItem.show(supportFragmentManager, "aaaaaa")
        }

        binding.headerAddNewOrder.ivHeaderBack.setOnClickListener {
            if (binding.edtAddNewProductName.getContentText().isEmpty()
                && binding.edtAddNewProductDescription.getContentText().isEmpty()
                && binding.edtAddNewProductQuantity.getContentText().isEmpty()
                && binding.edtAddNewProductVolume.getContentText().isEmpty()
                && binding.edtAddNewProductMass.getContentText().isEmpty()
                && binding.edtAddNewProductNumberOfCarton.getContentText().isEmpty()
                && binding.tvUriNewImageProduct.getContentText().isEmpty()
                && binding.tvAddOrderTypeCont.getContentText().isEmpty()
                && binding.gTvNewProductType.getContentText().isEmpty()
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
            val name = binding.edtAddNewProductName.getContentText()
            val des = binding.edtAddNewProductDescription.getContentText()
            val quantity = binding.edtAddNewProductQuantity.getContentText()
            var volume = 0.0
            var mass = 0.0
            var numberOfCarton = 0L
            var typeCont: String? = null
            if (viewModel.isLCL) {
                volume = binding.edtAddNewProductVolume.getContentText().toDouble()
                mass = binding.edtAddNewProductMass.getContentText().toDouble()
                numberOfCarton = binding.edtAddNewProductNumberOfCarton.getContentText().toLong()
                typeCont = binding.tvAddOrderTypeCont.getContentText()
            }
            val product =
                Product(
                    imgUri = viewModel.uri,
                    productName = name,
                    productDes = des,
                    quantity = quantity.toLong(),
                    volume = volume,
                    mass = mass,
                    numberOfCarton = numberOfCarton,
                    isOrderLCL = viewModel.isLCL,
                    type = binding.gTvNewProductType.getContentText(),
                    contType = typeCont
                )
            if (checkInvalidData()) {
                if(viewModel.isEdit){
                //    viewModel.updateProduct(product)
                }
                else{
                    showLoading()
                    viewModel.insertProduct(product)
                }
            }
        }
    }

    override fun observeData() {
        viewModel.insertProductLiveData.observe(this){
            hiddenLoading()
            if(it){
                val i = Intent()
                // i.putExtra(ADD_NEW_PRODUCT, Utils.g().getJsonFromObject(product))
                setResult(RESULT_OK, i)
                finish()
            }
            else{
                confirm.newBuild().setNotice(getString(R.string.str_add_new_product_tocart_fail))
            }
        }
    }

    private fun checkInvalidData(): Boolean {
        var view: View? = null
        var flag = false
        when {
            binding.tvUriNewImageProduct.getContentText().isEmpty() -> {
                binding.tvUriNewImageProduct.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.tvUriNewImageProduct
                flag = true
            }
            binding.edtAddNewProductName.getContentText().isEmpty() -> {
                binding.edtAddNewProductName.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtAddNewProductName
                flag = true
            }
            binding.edtAddNewProductDescription.getContentText().isEmpty() -> {
                binding.edtAddNewProductDescription.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtAddNewProductDescription
                flag = true
            }
            binding.edtAddNewProductQuantity.getContentText().isEmpty() -> {
                binding.edtAddNewProductQuantity.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtAddNewProductQuantity
                flag = true
            }
            binding.tvAddOrderTypeCont.getContentText().isEmpty() && !viewModel.isLCL -> {
                binding.tvAddOrderTypeCont.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.tvAddOrderTypeCont
                flag = true
            }
            binding.edtAddNewProductVolume.getContentText().isEmpty() && viewModel.isLCL -> {
                binding.edtAddNewProductVolume.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtAddNewProductVolume
                flag = true
            }
            binding.edtAddNewProductMass.getContentText().isEmpty() && viewModel.isLCL -> {
                binding.edtAddNewProductMass.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtAddNewProductMass
                flag = true
            }
            binding.edtAddNewProductNumberOfCarton.getContentText()
                .isEmpty() && viewModel.isLCL -> {
                binding.edtAddNewProductNumberOfCarton.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtAddNewProductNumberOfCarton
                flag = true
            }
            binding.gTvNewProductType.getContentText().isEmpty() -> {
                binding.gTvNewProductType.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.gTvNewProductType
                flag = true
            }
        }
        return if (flag) {
            view?.requestFocus()
            false
        } else
            true
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
        binding.tvAddOrderTypeCont.visibility = View.VISIBLE
        binding.tvAddNewProductVolumeTitle.visibility = View.GONE
        binding.edtAddNewProductVolume.visibility = View.GONE
        binding.tvAddNewProductMassTitle.visibility = View.GONE
        binding.edtAddNewProductMass.visibility = View.GONE
        binding.tvAddNewProductNumberOfCartonTitle.visibility = View.GONE
        binding.edtAddNewProductNumberOfCarton.visibility = View.GONE
        binding.tvUriNewImageProduct.setValueContent("")
        binding.edtAddNewProductName.setValueContent("")
        binding.edtAddNewProductDescription.setValueContent("")
        binding.edtAddNewProductQuantity.setValueContent("")
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
        binding.tvAddOrderTypeCont.visibility = View.GONE
        binding.tvAddNewProductVolumeTitle.visibility = View.VISIBLE
        binding.edtAddNewProductVolume.visibility = View.VISIBLE
        binding.tvAddNewProductMassTitle.visibility = View.VISIBLE
        binding.edtAddNewProductMass.visibility = View.VISIBLE
        binding.tvAddNewProductNumberOfCartonTitle.visibility = View.VISIBLE
        binding.edtAddNewProductNumberOfCarton.visibility = View.VISIBLE
        binding.tvUriNewImageProduct.setValueContent("")
        binding.edtAddNewProductName.setValueContent("")
        binding.edtAddNewProductDescription.setValueContent("")
        binding.edtAddNewProductQuantity.setValueContent("")
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).also {
            it.addCategory(Intent.CATEGORY_OPENABLE)
            it.type = "image/*"
            it.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        launcher.launch(intent)
    }

    @SuppressLint("SetTextI18n")
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
            binding.tvUriNewImageProduct.setValueContent(
                viewModel.uri.toString().substring(0, 40) + "..."
            )
        } else {
            Log.d(AppConstant.TAG, "result = null: ")
        }
    }
}