package com.kiran.softmandu.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kiran.softmandu.R
import com.kiran.softmandu.databinding.ActivityRegisterBinding
import com.kiran.softmandu.firebase.FirebaseHelper
import com.kiran.softmandu.model.User
import com.kiran.softmandu.utils.showSnackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddUser.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {

        val fname = binding.etFname.text.toString().trim()
        val lname = binding.etLname.text.toString().trim()
        val coventryId = binding.etCoventryID.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (password != binding.etConfirmPassword.text.toString()) {
            binding.etPassword.error = "Password does not match"
            binding.etPassword.requestFocus()
            return
        } else if (!binding.chkAgree.isChecked) {
            Toast.makeText(this, "Please agree terms and conditions", Toast.LENGTH_SHORT).show()
        } else {

            showProgressDialog()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        // Now add data to firestore database
                        val user = User(
                            firebaseUser.uid,
                            fname,
                            lname,
                            coventryId,
                            email
                        )
                        // Add other details to firestore database
                        FirebaseHelper().addUserToFirestore(this, user)
                    } else {
                        showMessageFromFirestore(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Register")
        progressDialog.setMessage("Registering user, please wait....")
        progressDialog.show()
    }

    fun closeProgressDialog() {
        progressDialog.dismiss()
    }

    fun showMessageFromFirestore(message: String, isError: Boolean) {
        closeProgressDialog()
        val snackbar =
            Snackbar.make(binding.contraintLayout, message, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        if (isError) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.red
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.teal_700
                )
            )
        }
        snackbar.show()
    }

}