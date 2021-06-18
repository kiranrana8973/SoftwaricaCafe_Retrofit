package com.kiran.softmandu.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.kiran.softmandu.R
import com.kiran.softmandu.databinding.ActivityLoginBinding
import com.kiran.softmandu.firebase.FirebaseHelper

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        binding.progressBar.visibility = View.VISIBLE
       // showProgressDialog()
        FirebaseHelper().checkUser(this, email, password)

    }

    private fun closeProgressDialog() {
        progressDialog.dismiss()
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.show()
    }

    fun showMessageFromFirestore(message: String, isError: Boolean) {
        binding.progressBar.visibility = View.GONE
        val snackBar =
            Snackbar.make(binding.linearLayout, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        if (isError) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
            snackBar.show()
        }
    }

    fun loadDashboard() {
        closeProgressDialog()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
    }
}