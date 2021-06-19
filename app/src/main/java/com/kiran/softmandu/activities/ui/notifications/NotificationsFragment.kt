package com.kiran.softmandu.activities.ui.notifications

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kiran.softmandu.R
import com.kiran.softmandu.databinding.DialogCustomImageSelectorBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.util.*

class NotificationsFragment : Fragment() {

    private lateinit var imgUser: ImageView
    private lateinit var imgChooseImage: ImageButton
    private lateinit var etFname: EditText
    private lateinit var eLtname: EditText
    private lateinit var binding: DialogCustomImageSelectorBinding
    private val IMAGE_DIRECTORY = "myImages"
    private lateinit var dialog: Dialog
    private var imagePath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        binding =
            DialogCustomImageSelectorBinding.inflate(layoutInflater)
        dialog = Dialog(requireActivity())
        dialog.setContentView(binding.root)
        imgUser = root.findViewById(R.id.imgUser)
        etFname = root.findViewById(R.id.etFname)
        eLtname = root.findViewById(R.id.etLname)
        imgChooseImage = root.findViewById(R.id.imgChooseImage)

        imgChooseImage.setOnClickListener {
            chooseImage()
        }

        return root
    }

    private fun chooseImage() {


        binding.btnLoadCamera.setOnClickListener {
            loadCamera()
            dialog.dismiss()
        }
        binding.btnLoadGallery.setOnClickListener {
            loadGallery()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun loadGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_CODE)
    }

    private val CAMERA_CODE = 1
    private val GALLERY_CODE = 2
    private fun loadCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                    // imgUser.setImageBitmap(thumbnail)
                    //OR
                    Glide.with(requireContext())
                        .load(thumbnail)
                        .centerCrop()
                        .into(imgUser)

                    imagePath = saveImageToInternalStorage(thumbnail)
                    Toast.makeText(requireContext(), imagePath, Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == GALLERY_CODE) {
                data?.let {
                    val selectedPhotoUri = data.data
                    Glide.with(requireContext())
                        .load(selectedPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Toast.makeText(
                                    requireContext(),
                                    "Error loading image",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource.let {
                                    val bitmap = resource!!.toBitmap()
                                    imagePath = saveImageToInternalStorage(bitmap)
                                }
                                return false
                            }
                        })
                        .into(imgUser)

                }
            }
        }
    }

    // It will return the absolute path
    fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        // file is the folder where we want to store and
        // imagename
        file = File(file, "${UUID.randomUUID()}.jpg")

        //Create an image
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            //compression process is finished so flush it
            stream.flush()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), e.printStackTrace().toString(), Toast.LENGTH_SHORT)
                .show()
        }

        return file.absolutePath
    }
}