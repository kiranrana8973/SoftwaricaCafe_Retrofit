package com.kiran.softmandu.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.kiran.softmandu.activities.LoginActivity
import com.kiran.softmandu.activities.RegisterActivity
import com.kiran.softmandu.activities.ui.dashboard.DashboardFragment
import com.kiran.softmandu.activities.ui.home.HomeFragment
import com.kiran.softmandu.activities.ui.notifications.NotificationsFragment
import com.kiran.softmandu.model.Item
import com.kiran.softmandu.utils.Constants
import com.kiran.softmandu.model.User
import java.util.*
import kotlin.collections.HashMap

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
                activity.showMessageFromFirestore(it.localizedMessage!!, true)
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

    // -------------------------ITEMS-------------------------

    fun getAllItems(fragment: HomeFragment) {
        val lstItems = mutableListOf<Item>()
        mFireStore.collection(Constants.TBL_ITEM)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    fragment.showErrorFromFireStore(error.localizedMessage)
                    return@addSnapshotListener
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED)
                        lstItems.add(dc.document.toObject(Item::class.java))
                }

                fragment.getItemsFromFireStore(lstItems)
            }

    }

//    private fun uploadImage(filePath: String) {
//
//        val ref = FirebaseStorage.getInstance()?.child("uploads/" + UUID.randomUUID().toString())
//        val uploadTask = ref?.putFile(filePath!!)
//
//        val urlTask =
//            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//                if (!task.isSuccessful) {
//                    task.exception?.let {
//                        throw it
//                    }
//                }
//                return@Continuation ref.downloadUrl
//            })?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val downloadUri = task.result
//                    addUploadRecordToDb(downloadUri.toString())
//                } else {
//                    // Handle failures
//                }
//            }?.addOnFailureListener {
//
//            }
//    }
//

    fun uploadImageToCloudStorage(
        fragment: NotificationsFragment,
        imageFileURI: Uri?,
        fileExtension: String
    ) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "User_profile" + System.currentTimeMillis() + ".$fileExtension"
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            // Get the downloadable URL from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadableImageURL = uri.toString()
                    // activity.imageUploadSuccess(downloadableImageURL)
                    fragment.imageUploadSuccess(downloadableImageURL)
                }
        }
            .addOnFailureListener { exception ->
                fragment.showErrorMessage(exception.localizedMessage)
            }

    }

    fun updateUserProfile(fragment: NotificationsFragment, userHasMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.TBL_USER)
            .document(getCurrentUserID())
            .update(userHasMap)
            .addOnSuccessListener {
                fragment.uploadSuccess("Updated successfuly")

            }
            .addOnFailureListener {
                fragment.uploadSuccess(it.localizedMessage.toString())
            }
    }
}