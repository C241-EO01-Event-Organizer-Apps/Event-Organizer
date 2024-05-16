package com.example.ptamanah.view.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ptamanah.R
import com.example.ptamanah.data.preference.UserPreference
import com.example.ptamanah.data.preference.dataStore
import com.example.ptamanah.data.repository.AuthRepo
import com.example.ptamanah.data.retrofit.ApiConfig
import com.example.ptamanah.view.eventTenant.DetailEventTenant
import com.example.ptamanah.view.main.MainActivity
import com.example.ptamanah.viewModel.event.EventTenantViewModel
import com.example.ptamanah.viewModel.factory.AuthViewModelFactory
import com.example.ptamanah.viewModel.main.MainViewModel

class SplashScreenActivity : AppCompatActivity() {

    private val userPreference: UserPreference by lazy { UserPreference(this.dataStore) }
    private val viewModelCashier: MainViewModel by viewModels {
        AuthViewModelFactory(AuthRepo(ApiConfig.getApiService(), userPreference))
    }
    private val viewModelTenant: EventTenantViewModel by viewModels {
        AuthViewModelFactory(AuthRepo(ApiConfig.getApiService(), userPreference))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            viewModelTenant.getSessionTenant().observe(this) { tenantSession ->
                if (tenantSession != null) {
                    startActivity(Intent(this, DetailEventTenant::class.java))
                }
            }

            viewModelCashier.getSession().observe(this) { cashierSession ->
                if (cashierSession != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}