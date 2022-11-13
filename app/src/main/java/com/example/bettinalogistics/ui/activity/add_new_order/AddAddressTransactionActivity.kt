package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityAddAddressTransactionBinding
import com.example.bettinalogistics.model.Order
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.ui.activity.gg_map.GoogleMapActivity
import com.example.bettinalogistics.ui.fragment.bottom_sheet.CustomerCompanyInfoBottomSheet
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddAddressTransactionActivity : BaseActivity() {
    companion object{
        const val NEW_ORDER = "newOrder"
        const val NEW_ADDRESS_ORDER ="newAddressOrder"
        fun startAddAddressTransactionActivity(context :Context, order: Order): Intent{
            val intent = Intent(context, AddAddressTransactionActivity::class.java)
            intent.putExtra(NEW_ORDER, Utils.g().getJsonFromObject(order))
            return intent
        }
    }

    override val viewModel: AddNewOrderViewModel by viewModel()

    override val binding: ActivityAddAddressTransactionBinding by lazy {
        ActivityAddAddressTransactionBinding.inflate(layoutInflater)
    }
    override fun initView() {
        val order = intent.getStringExtra(NEW_ORDER)
            ?.let { Utils.g().getObjectFromJson(it, Order::class.java) }
        viewModel.order = order
        Log.d(TAG, "initView: order = $order")
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
            viewModel.getUserCompanyInfo()
        }
    }
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val orderAddress =  Utils.g().getObjectFromJson(result.data?.getStringExtra(
                        NEW_ADDRESS_ORDER
                    ).toString(), OrderAddress::class.java)
                    viewModel.orderAddress = orderAddress
                    binding.tvOriginAddress.text = orderAddress?.address?.originAddress?:""
                    binding.tvDestinationAddress.text = orderAddress?.address?.originAddress?:""
                }
            }
        }
    override fun observeData() {
        viewModel.getUserCompanyInfoLiveData.observe(this){
            if(it != null){

            }
            else{
                val companyInfo = CustomerCompanyInfoBottomSheet()
                companyInfo.onConfirmListener = {company ->
                    viewModel.addCompanyInfo(company)
                }
                companyInfo.show(supportFragmentManager,"aaaaaaa")
            }
        }

    }

}