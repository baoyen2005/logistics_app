package com.example.bettinalogistics.ui.activity.add_new_order

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.baseapp.BaseActivity
import com.example.baseapp.view.removeAccentNormalize
import com.example.baseapp.view.setSafeOnClickListener
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivityAddAddressTransactionBinding
import com.example.bettinalogistics.model.OrderAddress
import com.example.bettinalogistics.model.Product
import com.example.bettinalogistics.ui.activity.confirm_order.ConfirmOrderTransportationActivity
import com.example.bettinalogistics.ui.activity.gg_map.GoogleMapActivity
import com.example.bettinalogistics.ui.fragment.bottom_sheet.ChooseTypeTransportationBottomSheet
import com.example.bettinalogistics.ui.fragment.bottom_sheet.CustomerCompanyInfoBottomSheet
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class AddAddressTransactionActivity : BaseActivity() {
    companion object{
        const val NEW_ORDER = "newOrder"
        const val NEW_ADDRESS_ORDER ="newAddressOrder"
        fun startAddAddressTransactionActivity(context: Context, products: List<Product>): Intent {
            val intent = Intent(context, AddAddressTransactionActivity::class.java)
            intent.putExtra(NEW_ORDER, Utils.g().getJsonFromObject(products))
            return intent
        }
    }

    override val viewModel: AddNewProductViewModel by viewModel()

    override val binding: ActivityAddAddressTransactionBinding by lazy {
        ActivityAddAddressTransactionBinding.inflate(layoutInflater)
    }
    override fun initView() {
        viewModel.products  = Utils.g().provideGson()
            .fromJson(intent.getStringExtra(NEW_ORDER), object :
                TypeToken<List<Product>>() {}.type)?:  listOf()
        Log.d(TAG, "initView: order = ${viewModel.products}")
        binding.layoutHeaderOrder.tvHeaderTitle.text = getString(R.string.str_infor_order)
        binding.layoutHeaderOrder.ivHeaderBack.setOnClickListener {
            finish()
        }
        viewModel.orderAddress = Utils.g().getDataString(DataConstant.ORDER_ADDRESS)
            ?.let { Utils.g().getObjectFromJson(it, OrderAddress::class.java) }
        viewModel.typeTransaction = Utils.g().getDataString(DataConstant.ORDER_TRANSPORT_TYPE)
        viewModel.methodTransaction = Utils.g().getDataString(DataConstant.ORDER_TRANSPORT_METHOD)
        if(!viewModel.orderAddress.isNullOrEmpty())
        binding.linearRoadTransport.isVisible = binding.edtOriginSearch.getContentText().contains(getString(R.string.str_korea))
    }

    override fun initListener() {
        binding.btnAddAddressContinued.setSafeOnClickListener {
            if (checkValidate()) {
                showLoading()
                setUpOriginLatLon()
                setUpDestinationLatLon()
                viewModel.orderAddress?.originAddress =
                    binding.edtOriginSearch.getContentText()
                viewModel.orderAddress?.destinationAddress =?
                        +









































                        .
                    binding.edtDestinationSearch.getContentText()
                viewModel.getUserCompanyInfo()
            }
        }
        binding.llChinhNgach.setOnClickListener {
            binding.icCheckBoxChinhNgach.setImageResource(R.drawable.ic_checkbox_checked)
            binding.ivCheckBoxTieuNgach.setImageResource(R.drawable.ic_checkbox_uncheck)
            viewModel.methodTransaction = binding.tvChinhNgachTitle.text.toString()
        }
        binding.llTieuNgach.setOnClickListener {
            binding.ivCheckBoxTieuNgach.setImageResource(R.drawable.ic_checkbox_checked)
            binding.icCheckBoxChinhNgach.setImageResource(R.drawable.ic_checkbox_uncheck)
            viewModel.methodTransaction = binding.tvTieuNgachTitle.text.toString()
        }
        binding.linearRoadTransport.setOnClickListener {
            binding.linearRoadTransport.setBackgroundResource(R.drawable.shape_ffffff_stroke_004a9c_corner_12)
            binding.linearSeaTransport.setBackgroundResource(R.drawable.shape_bg_fffff_corner_12)
            viewModel.typeTransaction = getString(R.string.str_road_transport)
        }
        binding.linearSeaTransport.setOnClickListener {
            binding.linearSeaTransport.setBackgroundResource(R.drawable.shape_ffffff_stroke_004a9c_corner_12)
            binding.linearRoadTransport.setBackgroundResource(R.drawable.shape_bg_fffff_corner_12)
            viewModel.typeTransaction = getString(R.string.str_sea_transport)
        }
    }

    private fun checkValidate(): Boolean {
        var view: View? = null
        var flag = false
        when {
            binding.edtOriginSearch.getContentText().isEmpty() -> {
                binding.edtOriginSearch.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtOriginSearch
                flag = true
            }
            binding.edtDestinationSearch.getContentText().isEmpty() -> {
                binding.edtDestinationSearch.setVisibleMessageError(getString(R.string.invalid_field))
                view = binding.edtDestinationSearch
                flag = true
            }
            viewModel.typeTransaction.isNullOrEmpty() ->{
                binding.tvErrorTypeTransaction.visibility = View.VISIBLE
                binding.tvErrorTypeTransaction.text = getString(R.string.tr_choose_type_transportation)
                flag = true
                view = binding.tvErrorTypeTransaction
            }
            viewModel.methodTransaction.isNullOrEmpty() ->{
                binding.tvErrorMethodTransaction.visibility = View.VISIBLE
                binding.tvErrorMethodTransaction.text = getString(R.string.tr_choose_method_transportation)
                flag = true
                view = binding.tvErrorMethodTransaction
            }
        }
        return if (flag) {
            view?.requestFocus()
            false
        } else {
            binding.tvErrorTypeTransaction.visibility = View.GONE
            binding.tvErrorMethodTransaction.visibility = View.GONE
            true
        }
    }

    override fun observeData() {
        viewModel.getUserCompanyInfoLiveData.observe(this){
            hiddenLoading()
            if(it != null) {
                viewModel.userCompany = it
                Utils.g().saveDataString(
                    DataConstant.ORDER_ADDRESS,
                    Utils.g().getJsonFromObject(viewModel.orderAddress)
                )
                Utils.g().saveDataString(DataConstant.ORDER_TRANSPORT_TYPE, viewModel.typeTransaction)
                Utils.g().saveDataString(DataConstant.ORDER_TRANSPORT_METHOD, viewModel.methodTransaction)
                startActivity(
                    viewModel.products?.let { producs ->
                        viewModel.orderAddress?.let { orderAddress ->
                            ConfirmOrderTransportationActivity.startConfirmOrderActivity(
                                context = this,
                                product = producs,
                                orderAddress = orderAddress,
                                typeTransport = viewModel.typeTransaction?:"",
                                methodTransport = viewModel.methodTransaction?:"",
                                userCompany = it
                            )
                        }
                    }
                )
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

    private fun setUpOriginLatLon() {
        val locationOrigin = binding.edtOriginSearch.getContentText().removeAccentNormalize()
        var addressList: List<Address>? = null;
        if (locationOrigin.isNotEmpty()) {
            val geocoder = Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locationOrigin, 1);
            } catch (e: IOException) {
                e.printStackTrace();
            }
            val address = addressList?.get(0);
            val latLng = address?.let { LatLng(it.latitude, it.longitude) };
            viewModel.latLonOriginAddress = latLng
        }
    }

    private fun setUpDestinationLatLon() {
        val locationDestination = binding.edtDestinationSearch.getContentText().removeAccentNormalize()
        var addressList: List<Address>? = null;
        if (locationDestination.isNotEmpty()) {
            val geocoder = Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(locationDestination, 1);
            } catch (e: IOException) {
                e.printStackTrace();
            }
            val address = addressList?.get(0);
            if(address == null){
                confirm.setNotice(getString(R.string.str_choose_address_again))
            }
            else{
                val latLng = address.let { LatLng(it.latitude, it.longitude) };
                viewModel.latLonDestinationAddress = latLng
            }
        }
    }
}