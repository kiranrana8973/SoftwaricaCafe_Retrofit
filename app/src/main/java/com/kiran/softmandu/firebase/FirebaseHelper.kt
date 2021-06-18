package com.kiran.softmandu.firebase

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kiran.softmandu.activities.LoginActivity
import com.kiran.softmandu.activities.RegisterActivity
import com.kiran.softmandu.utils.Constants
import com.kiran.softmandu.model.User

class FirebaseHelper {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun addUserToFirestore(activity: RegisterActivity, user: User) {
        mFireStore.collection(Constants.TBL_USER)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.showMessageFromFirestore("Registered Successfully", false)
            }
            .addOnFailureListener {
                activity.showMessageFromFirestore(it.localizedMessage, true)
            }
    }

    fun checkUser(activity: LoginActivity, email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getCurrentUserDetails(activity)
                } else {
                    activity.showMessageFromFirestore(
                        "Either username or password is incorrect",
                        true
                    )
                }
            }
    }

    fun getCurrentUserID(): String {
        // We are getting loggedin userid from authentication not from firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getCurrentUserDetails(activity: LoginActivity) {
        mFireStore.collection(Constants.TBL_USER)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)

                //Save user details to shared preferences
                val sharedPreferences = activity.getSharedPreferences(
                    Constants.USER_SHARED_PREF,
                    Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user!!.firstName} ${user.lastName}"
                )
                editor.apply()

                activity.loadDashboard()
            }
            .addOnFailureListener { e ->
                activity.showMessageFromFirestore(e.localizedMessage!!, true)
            }
    }

}