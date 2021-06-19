package com.kiran.softmandu.activities.ui.home

import android.app.ProgressDialog
import android.content.ClipData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kiran.softmandu.R
import com.kiran.softmandu.databinding.FragmentHomeBinding
import com.kiran.softmandu.firebase.FirebaseHelper
import com.kiran.softmandu.model.Item

class HomeFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding : FragmentHomeBinding
    private lateinit var textView : TextView
    private lateinit var imageView : ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val lstItems = FirebaseHelper().getAllItems(this)

        textView = root.findViewById(R.id.textView)
        imageView = root.findViewById(R.id.imageView)


        return root
    }

    fun getItemsFromFireStore(lstItems : List<Item>){
        textView.text ="No of items :  ${lstItems.size}"

        lstItems[0].imageUrl
        Glide.with(requireActivity())
            .load(lstItems[0].imageUrl)
            .into(imageView)

    }

    fun showErrorFromFireStore(message : String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    fun showProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Loading items, please wait....")
        progressDialog.show()
    }

    fun closeProgressDialog() {
        progressDialog.dismiss()
    }
//    fun showMessageFromFirestore(message: String, isError: Boolean) {
//        closeProgressDialog()
//        val snackbar =
//            Snackbar.make(binding.contraintLayout, message, Snackbar.LENGTH_LONG)
//        val snackBarView = snackbar.view
//        if (isError) {
//            snackBarView.setBackgroundColor(
//                ContextCompat.getColor(
//                    this,
//                    R.color.red
//                )
//            )
//        } else {
//            snackBarView.setBackgroundColor(
//                ContextCompat.getColor(
//                    this,
//                    R.color.teal_700
//                )
//            )
//        }
//        snackbar.show()
//    }
}