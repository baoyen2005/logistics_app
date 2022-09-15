package com.example.bettinalogistics.ui.signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.baseapp.BaseActivity
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.ActivitySignUpBinding
import com.example.bettinalogistics.utils.AppConstant.Companion.CANCEL_DATE_PICKER_VAL
import com.example.bettinalogistics.utils.AppConstant.Companion.CHOOSE_IMAGE
import com.example.bettinalogistics.utils.AppPermissionsUtils
import com.example.bettinalogistics.utils.State
import com.example.bettinalogistics.utils.dateToString
import com.example.bettinalogistics.utils.stringToDate
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SignUpActivity : BaseActivity() {
    private lateinit var appPermissions: AppPermissionsUtils
    private lateinit var datePickerDialog: DatePickerDialog


    companion object {
        private final val TAG = "SignupActivity"

        fun startSignUpActivity(context: Context) {
            Intent(context, SignUpActivity::class.java)
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_login

    override val viewModel: SignUpViewModel by viewModel()

    override val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onReady(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        appPermissions = AppPermissionsUtils()
        Log.d(TAG, "onReady: binding = $binding")
        binding.imgClickCamera.setOnClickListener {
            if (appPermissions.isStorageOk(applicationContext))
                pickImage()
            else
                appPermissions.requestStoragePermission(this)
        }

        binding.imgDatePicker.setOnClickListener {
            chooseDate()
        }

        binding.btnSignup.setOnClickListener {
            saveUser()
        }
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
            val photoUri: Uri? = result.data!!.data
            binding.imgAvt.visibility = View.VISIBLE
            binding.imgClickCamera.visibility = View.GONE
            binding.imgAvt.setImageURI(photoUri)
        } else {
            binding.imgAvt.visibility = View.GONE
            binding.imgClickCamera.visibility = View.VISIBLE
            showToast(CHOOSE_IMAGE)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun chooseDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(supportFragmentManager, getString(R.string.date_picker))

        datePicker.addOnPositiveButtonClickListener {
            val date = dateToString(Date(it))
            binding.tvDateOfBirthSignUp.text = date
        }
        datePicker.addOnNegativeButtonClickListener {
            showToast("${datePicker.headerText} is cancelled")
        }
        datePicker.addOnCancelListener {
            showToast(CANCEL_DATE_PICKER_VAL)
        }
    }

    private fun areFieldReady(): Boolean {
        val fullName = binding.edtFullNameSignUp.text.trim().toString()
        val phone = binding.edtPhoneSignup.text.trim().toString()
        val dateOfBirth = binding.tvDateOfBirthSignUp.text.trim().toString()
        val email = binding.edtEmailSignUp.text.trim().toString()
        val password = binding.edtPasswordSignup.text.trim().toString()
        val address = binding.edtAddressSignUp.text.trim().toString()

        var view: View? = null
        var flag = false

        when {
            fullName.isEmpty() -> {
                binding.edtFullNameSignUp.error = getString(R.string.invalid_field)
                view = binding.edtFullNameSignUp
                flag = true
            }

            phone.isEmpty() -> {
                binding.edtPhoneSignup.error = getString(R.string.invalid_field)
                view = binding.edtPhoneSignup
                flag = true
            }

            dateOfBirth.isEmpty() -> {
                binding.tvDateOfBirthSignUp.error = getString(R.string.invalid_field)
                view = binding.tvDateOfBirthSignUp
                flag = true
            }

            email.isEmpty() -> {
                binding.edtEmailSignUp.error = getString(R.string.invalid_field)
                view = binding.edtEmailSignUp
                flag = true
            }
            password.isEmpty() -> {
                binding.edtPasswordSignup.error = getString(R.string.invalid_field)
                view = binding.edtPasswordSignup
                flag = true
            }
            password.length < 8 -> {
                binding.edtPasswordSignup.error = getString(R.string.invalid_pass)
                view = binding.edtPasswordSignup
                flag = true
            }

            address.isEmpty() -> {
                binding.edtAddressSignUp.error = getString(R.string.invalid_field)
                view = binding.edtAddressSignUp
                flag = true
            }
            else -> {
                val uri = viewModel.getUri()
                val dateOB = stringToDate(dateOfBirth)
                if (uri != null) {
                    viewModel.signUp(
                        uri, fullName, phone, dateOB!!,
                        password, email, address
                    )
                } else {
                    Log.d(TAG, "areFieldReady: uri null")
                }
            }
        }

        return if (flag) {
            view?.requestFocus()
            false
        } else
            true
    }

    private fun saveUser() {
                lifecycleScope.launchWhenCreated {
                    viewModel.stateSignInFlow
                        .collectLatest {
                            when (it) {
                                is State.Loading -> {
                                    if (it.flag == true)
                                        showLoading()
                                }

                                is State.Success -> {
                                    hiddenLoading()
                                    Snackbar.make(
                                        binding.root,
                                        it.data.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                    onBackPressed()

                                }
                                is State.Failed -> {
                                    hiddenLoading()
                                    Snackbar.make(
                                        binding.root,
                                        it.error,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {}
                            }
                    }
                }
            }
}