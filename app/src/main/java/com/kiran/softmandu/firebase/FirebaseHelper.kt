package com.kiran.softmandu.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kiran.softmandu.activities.RegisterActivity
import com.kiran.softmandu.utils.Constants
import com.kiran.softmandu.model.User

class FirebaseHelper {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun addUserToFirestore(activity : RegisterActivity ,user: User) {
        mFireStore.collection(Constants.tbl_User)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.showMessageFromFirestore("Registered Successfully",false)
            }
            .addOnFailureListener {
                activity.showMessageFromFirestore(it.localizedMessage,true)
            }
    }

}