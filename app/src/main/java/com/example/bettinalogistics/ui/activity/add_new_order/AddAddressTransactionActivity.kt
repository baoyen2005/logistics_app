package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityAddAddressTransactionBinding
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.ui.activity.confirm_order.ConfirmOrderTransportationActivity
import com.example.bettinalogistics.ui.activity.gg_map.GoogleMapActivity
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ChooseTypeTransportationBottomSheet
import com.example.bettinalogistics.ui.fragment.bottom_sheet.CustomerCompanyInfoBottomSheet
import com.example.bettinalogistics.utils.Utils
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddAddressTransactionActivity : BaseActivity() {
    companion object{
        const val NEW_ORDER = "newOrder"
        const val NEW_ADDRESS_ORDER ="newAddressOrder"
        fun startAddAddressTransactionActivity(context: Context, product: List<Product>): Intent {
            val intent = Intent(context, AddAddressTransactionActivity::class.java)
            intent.putExtra(NEW_ORDER, Utils.g().getJsonFromObject(product))
            return intent
        }
    }

    override val viewModel: AddNewOrderViewModel by viewModel()

    override val binding: ActivityAddAddressTransactionBinding by lazy {
        ActivityAddAddressTransactionBinding.inflate(layoutInflater)
    }
    override fun initView() {
        val products: List<Product> = Utils.g().provideGson().fromJson(
            intent.getStringExtra(NEW_ORDER),
            object : TypeToken<List<Product?>>() {}.type
        )
        viewModel.products = products
        Log.d(TAG, "initView: order = $products")
        binding.layoutHeaderOrder.tvHeaderTitle.text = getString(R.string.str_address_order)
        binding.layoutHeaderOrder.ivHeaderBack.setOnClickListener {
            finish()
        }
    }

    override fun initListener() {
        binding.tvChooseOriginAddress.setSafeOnClickListener {
            resultLauncher.launch(Intent(this, GoogleMapActivity::class.java))
        }
        binding.btnAddAddressContinued.setSafeOnClickListener {
            if (checkValidate()) {
                showLoading()
                viewModel.orderAddress?.address?.originAddress =
                    binding.edtDetailOriginAddressInput.getContentText() + " " +
                            binding.tvOriginAddress.text.toString()
                viewModel.orderAddress?.address?.destinationAddress =
                    binding.edtDetailDestinationAddressInput.getContentText() + " " +
                            binding.tvDestinationAddress.text.toString()
                viewModel.getUserCompanyInfo()
            }
        }
    }

    private fun checkValidate(): Boolean {
        var view: View? = null
        var flag = false
        when {
            binding.tvOriginAddress.text.isNullOrEmpty() -> {
                binding.tvOriginAddress.error = getString(R.string.invalid_field)
                view = binding.tvOriginAddress
                flag = true
            }
            binding.edtDetailOriginAddressInput.getContentText().isEmpty() -> {
                binding.edtDetailOriginAddressInput.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtDetailOriginAddressInput
                flag = true
            }
            binding.tvDestinationAddress.text.isNullOrEmpty() -> {
                binding.tvDestinationAddress.error = getString(R.string.invalid_field)
                view = binding.tvDestinationAddress
                flag = true
            }
            binding.edtDetailDestinationAddressInput.getContentText().isEmpty() -> {
                binding.edtDetailDestinationAddressInput.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtDetailDestinationAddressInput
                flag = true
            }
        }
        return if (flag) {
            view?.requestFocus()
            false
        } else
            true
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val orderAddress = Utils.g().getObjectFromJson(
                        result.data?.getStringExtra(
                            NEW_ADDRESS_ORDER
                        ).toString(), OrderAddress::class.java
                    )
                    viewModel.orderAddress = orderAddress
                    binding.tvOriginAddress.text = orderAddress?.address?.originAddress ?: ""
                    binding.tvDestinationAddress.text = orderAddress?.address?.destinationAddress?:""
                }
            }
        }
    override fun observeData() {
        viewModel.getUserCompanyInfoLiveData.observe(this){
            hiddenLoading()
            if(it != null) {
                viewModel.userCompany = it
                val chooseTypeTransportationBottomSheet = ChooseTypeTransportationBottomSheet()
                chooseTypeTransportationBottomSheet.confirmChooseTypeTransaction = { type, method ->
                    startActivity(
                        viewModel.products?.let { products ->
                            viewModel.orderAddress?.let { orderAddress ->
                                ConfirmOrderTransportationActivity.startConfirmOrderActivity(
                                    context = this,
                                    products = products,
                                    orderAddress = orderAddress,
                                    typeTransport = type,
                                    methodTransport = method,
                                    userCompany = it
                                )
                            }
                        }
                    )
                }
                chooseTypeTransportationBottomSheet.show(supportFragmentManager, "sssssss")
            } else {
                confirm.newBuild().setNotice(getString(R.string.str_new_company))
                    .addButtonAgree {
                        val companyInfo = CustomerCompanyInfoBottomSheet()
                        companyInfo.onConfirmListener = { company ->
                            showLoading()
                            viewModel.addCompanyInfo(company)
                        }
                        companyInfo.show(supportFragmentManager, "aaaaaaa")
                    }
            }
        }
        viewModel.addCompanyInfoLiveData.observe(this) {
            hiddenLoading()
            if (it) {
                confirm.setNotice(getString(R.string.str_add_company_success))
                viewModel.getUserCompanyInfo()
            } else confirm.setNotice(getString(R.string.str_add_company_failed))
        }
    }

}