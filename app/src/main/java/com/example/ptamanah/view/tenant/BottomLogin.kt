package com.example.ptamanah.view.tenant

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ptamanah.data.preference.UserPreference
import com.example.ptamanah.data.preference.dataStore
import com.example.ptamanah.data.repository.AuthRepo
import com.example.ptamanah.data.retrofit.ApiConfig
import com.example.ptamanah.databinding.FragmentBottomLoginBinding
import com.example.ptamanah.viewModel.factory.AuthViewModelFactory
import com.example.ptamanah.viewModel.login.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class BottomLogin : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomLoginBinding
    private var email: String = ""
    private var eventId: String = ""
    private val userPreference: UserPreference by lazy { UserPreference(requireContext().dataStore) }
    private val loginViewModel: LoginViewModel by viewModels {
        AuthViewModelFactory(AuthRepo(ApiConfig.getApiService(), userPreference))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventId = arguments?.getString(EVENT_ID).toString()

        loginViewModel.getEmail().observe(this@BottomLogin) { user ->
            email = user.toString()
        }

        binding.checkEventButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString().trim()

            if (password.isEmpty()) {
                binding.passwordEditTextLayout.error =
                    "Password harus diisi"
            } else {
                if (isInternetAvailable(requireContext())) {
                    lifecycleScope.launch {
                        loginViewModel.loginTenant(email, password, eventId).collect { result ->
                            result.onSuccess { credential ->

                                if (credential.info == "Login berhasil") {
                                    showToast("Berhasil masuk")

                                    loginViewModel.saveSessionTenant(credential.data?.token.toString())
                                    val intent = Intent(context, DetailEventTenant::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                } else {
                                    showToast("Silahkan periksa password anda kembali")
                                    dismiss()
                                }

                            }
                            result.onFailure {
                                showToast("Akun Anda Sudah Melewati Masa Aktif")
                                dismiss()
                            }
                        }
                    }
                } else {
                    showToast("Silahkan periksa internet anda terlebih dahulu")
                    dismiss()
                }
            }
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "AccountConfirmationBottomSheet"
        const val EVENT_ID = "event_id"

        fun newInstance(eventId: String): BottomLogin {
            val fragment = BottomLogin()
            val args = Bundle()
            args.putString(EVENT_ID, eventId)
            fragment.arguments = args
            return fragment
        }

    }
}