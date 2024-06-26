package com.example.ptamanah.view.tenant

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ptamanah.R
import com.example.ptamanah.data.preference.UserPreference
import com.example.ptamanah.data.preference.dataStore
import com.example.ptamanah.data.repository.AuthRepo
import com.example.ptamanah.data.retrofit.ApiConfig
import com.example.ptamanah.databinding.ActivityDetailEventTenantBinding
import com.example.ptamanah.view.camera.CameraTenant
import com.example.ptamanah.view.camera.CameraTenant.Companion.ID_EVENT_TENANT
import com.example.ptamanah.view.tenant.LogCheckinTenant.Companion.TOKEN_ID
import com.example.ptamanah.view.main.HomePageCashier
import com.example.ptamanah.viewModel.event.EventTenantViewModel
import com.example.ptamanah.viewModel.factory.AuthViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DetailEventTenant : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventTenantBinding
    private val userPreference: UserPreference by lazy { UserPreference(this.dataStore) }
    private val viewModel: EventTenantViewModel by viewModels {
        AuthViewModelFactory(AuthRepo(ApiConfig.getApiService(), userPreference))
    }
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventTenantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        viewModel.getSessionTenant().observe(this) {
            token = it.toString()
            getTenantProfile()
            refresh()
        }

    }

    private fun logoutApi() {
        val currentToken = token
        lifecycleScope.launch {
            viewModel.logoutTenant(currentToken).collect { result ->
                result.onSuccess {
                    Toast.makeText(this@DetailEventTenant, "Berhasil logout", Toast.LENGTH_SHORT).show()
                }
                result.onFailure {
                    Toast.makeText(this@DetailEventTenant, "Gagal keluar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getTenantProfile() {
        lifecycleScope.launch {
            viewModel.getTenantProfile(token).collect {result ->
                result.onSuccess { data ->
                    binding.apply {
                        namaTenant.text = data.data?.namaPerusahaan
                        kodeTenant.text = data.data?.kodeTenant
                        namaEvent.text = data.data?.event?.namaEvent
                        namaOrgani.text = data.data?.event?.namaOrganizer
                        jumlahPengunjung.text = data.data?.totalTenantVisitors.toString()

                    }
                    binding.btnQr.setOnClickListener {
                        val intent = Intent(this@DetailEventTenant, CameraTenant::class.java)
                        intent.putExtra(ID_EVENT_TENANT, data.data?.eventId)
                        startActivity(intent)
                    }
                    binding.btnLihatDetail.setOnClickListener {
                        val intent = Intent(this@DetailEventTenant, LogCheckinTenant::class.java)
                        intent.putExtra(TOKEN_ID, token)
                        intent.putExtra(ID_EVENT_TENANT, data.data?.eventId)
                        startActivity(intent)
                    }

                }
                result.onFailure {
                    Snackbar.make(binding.root, "Silahkan periksa internet anda terlebih dahulu", Snackbar.LENGTH_LONG).show()

                }
            }
        }
    }
    private fun refresh() {
        binding.btnRefresh.setOnClickListener {
            getTenantProfile()
        }
    }

private fun setupActionBar() {
    supportActionBar?.apply {
        setDisplayShowTitleEnabled(false)
        setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@DetailEventTenant, R.color.biru_toska)))

        val customActionBar = LayoutInflater.from(this@DetailEventTenant).inflate(R.layout.actionbar_main, null)
        val actionBarParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        setDisplayShowCustomEnabled(true)
        setCustomView(customActionBar, actionBarParams)
    }
}

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_login -> {
                    AlertDialog.Builder(this).apply {
                        setMessage("Apakah anda yakin ingin keluar?")
                        setPositiveButton("Ok") { _, _ ->
                            logoutApi()
                            viewModel.logout()
                            val intent = Intent(context, HomePageCashier::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}