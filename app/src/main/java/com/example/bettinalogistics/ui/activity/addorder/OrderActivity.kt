package com.example.bettinalogistics.ui.activity.addorder

import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityOrderBinding
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.ui.activity.add_new_order.AddAddressTransactionActivity
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewOrderActivity
import com.example.bettinalogistics.ui.activity.add_new_order.AddNewOrderActivity.Companion.ADD_NEW_PRODUCT
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.example.bettinalogistics.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderActivity : BaseActivity() {
    private lateinit var addOrderAdapter: AddOrderAdapter

    override val viewModel: OrderViewModel by viewModel()

    override val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }

    override fun initView() {
        addOrderAdapter = AddOrderAdapter()
        binding.layoutHeaderOrder.tvHeaderTitle.text = getString(R.string.header_product)
        binding.rvOrderList.adapter = addOrderAdapter
    }

    override fun initListener() {
        binding.layoutHeaderOrder.ivHeaderBack.setOnClickListener {
            if(viewModel.productList.isEmpty()){
                finish()
            }
            else {
                confirm.newBuild().setNotice(getString(R.string.str_confirm_order))
                    .addButtonAgree {
                        confirm.dismiss()
                    }.addButtonCancel(getString(com.example.baseapp.R.string.cancel)) {
                        confirm.dismiss()
                        finish()
                    }
            }
        }
        binding.btnAddOrderProduct.setOnClickListener {
            val intent =  Intent(this, AddNewOrderActivity::class.java)
            resultLauncher.launch(intent)
        }
        binding.btnOrderContinued.setOnClickListener {
//            showLoading()
    //        viewModel.addOrder(order)
            resultLauncherAddAddress.launch(AddAddressTransactionActivity.startAddAddressTransactionActivity(this, viewModel.productList))
        }
    }

    override fun observeData(){
        viewModel.addOrderLiveData.observe(this){
            Log.d(TAG, "observerData: $it")
            if(it){
                hiddenLoading()
                confirm.newBuild().setNotice(getString(R.string.str_add_order_success)).addButtonAgree{
                    finish()
                }
            }
            else
            {
                hiddenLoading()
                confirm.newBuild().setNotice(getString(R.string.str_add_order_fail)).addButtonAgree{
                    finish()
                }
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val product =  Utils.g().getObjectFromJson(result.data?.getStringExtra(ADD_NEW_PRODUCT).toString(),Product::class.java)
                    product?.let { viewModel.productList.add(it) }
                    addOrderAdapter.resetOrderList(viewModel.productList)
                }
            }
        }
private var resultLauncherAddAddress =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    finish()
                }
            }
        }

}