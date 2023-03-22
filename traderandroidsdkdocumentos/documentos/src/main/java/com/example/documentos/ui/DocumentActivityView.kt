package com.example.documentos.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.documentos.R
import com.example.documentos.databinding.ActivityDocumentViewBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File

class DocumentActivityView : AppCompatActivity(), IDocument.View {

    private lateinit var binding: ActivityDocumentViewBinding
    private lateinit var presenter: DocumentPresenter
    private val MY_PERMISSIONS_REQUEST_CAMERA = 100
    private val FILE_SELECT_CODE = 101
    private lateinit var mStorage: FirebaseStorage
    val myRef = Firebase.database.getReference("file")
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Modify bar
        modifiColorBar()
        presenter = DocumentPresenter(this, this, this)
        FirebaseApp.initializeApp(this)
        mStorage = FirebaseStorage.getInstance()
        iniComponent()
    }

    //Component
    private fun iniComponent() {
        presenter.permissionStorage()
        binding.progressBar.visibility = View.GONE
        binding.selectPhoto.setOnClickListener {
            checkCameraPermission()
        }
        binding.selectGalery.setOnClickListener {
            presenter.onGalleryButtonClicked()
        }

        binding.uploadFile.setOnClickListener {
            presenter.onButtonClicked(selectPDFFile)
        }
        binding.toolbarBack.idBackOperator.setOnClickListener {
            finish()
        }
    }

    //Modify bar
    private fun modifiColorBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Cambia el color de la barra de notificaciones a rojo
            window.statusBarColor = ContextCompat.getColor(this, R.color.teal_200)

            // Aplica los cambios
            window.apply {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }

    //Check that the file is less than 4mb
    private var selectPDFFile = registerForActivityResult(ActivityResultContracts.GetContent()) {
        // contentResolver(it)
        val fd = it?.let { it1 -> contentResolver.openFileDescriptor(it1, "r") }
        val size = fd?.statSize
        val fileObtainedSize = size?.div(1024)
        val file = File(it?.path!!)
        binding.progressBar.visibility = View.VISIBLE
        if (fileObtainedSize != null) {
            //Validate that the file is less than 4MB
            if (fileObtainedSize < 4000) {
                // Upload the file to storage firebase
                val storageRef = mStorage.reference.child("file")
                val fileName: StorageReference =
                    storageRef.child("img-" + it.lastPathSegment + System.currentTimeMillis() + ".jpg")
                fileName.putFile(it).addOnSuccessListener { uri ->
                    val hashMap = HashMap<String, String>()
                    hashMap["link"] = java.lang.String.valueOf(uri)
                    myRef.setValue(hashMap)
                    Toast.makeText(this,
                        getString(R.string.msg_file_big_succes),
                        Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }

                //Subir a fireBase Storage
            } else {
                Toast.makeText(this, getString(R.string.msg_file_big), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Display the photo in an ImageView
    override fun showPhoto(photo: Bitmap) {
        binding.imgPhoto.setImageBitmap(photo)
    }

    // Display an error message in a TextView
    override fun showErrorMessage(message: String) {

    }

    // Open the image gallery
    override fun showGallerySelection() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    // Open the camera to take a photo
    override fun showCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }


    //Gets the obtained image and converts it to bitmap
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {//Galery
                    val uri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    presenter.onPhotoTaken(bitmap)
                    //upload Photos to Storage
                    uploadPhotosStorage(bitmap)
                }
                REQUEST_CODE_CAMERA -> {//Camera

                    val bitmap = data?.extras?.get("data") as Bitmap
                    presenter.onPhotoTaken(bitmap)
                    //upload Photos to Storage
                    uploadPhotosStorage(bitmap)
                }
                FILE_SELECT_CODE -> {//validate file
                    data?.data?.let { uri ->
                        if (uri != null) {
                            presenter.onFileSelected(uri)
                        }
                    }
                }
            }
        } else {
            presenter.onError(getString(R.string.error_getting_image))
        }

    }

    //upload Photos to Storage
    fun uploadPhotosStorage(bitmap: Bitmap) {
        binding.progressBar.visibility = View.VISIBLE
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                bitmap,
                "Title",
                null)
        var uriImg = Uri.parse(path)
        val storageRef = mStorage.reference.child("image")
        val fileName: StorageReference =
            storageRef.child("img-" + uriImg?.lastPathSegment + System.currentTimeMillis() + ".jpg")
        if (uriImg != null) {
            fileName.putFile(uriImg).addOnSuccessListener { uri ->
                val hashMap = HashMap<String, String>()
                hashMap["link"] = java.lang.String.valueOf(uri)
                myRef.setValue(hashMap)
                Toast.makeText(this,
                    getString(R.string.msg_photo_succes),
                    Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        const val REQUEST_CODE_GALLERY = 100
        const val REQUEST_CODE_CAMERA = 200
    }

    //Permission camera
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //The permission is not accepted.
            requestCameraPermission()
        } else {
            //The permission is accepted.
            presenter.onCameraButtonClicked()
        }
    }


    //Permission check
    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)
        ) {
            //The user has already refused the permission before, we must inform him to go to settings.
            Toast.makeText(this, getString(R.string.msg_invalid_permission_msg), Toast.LENGTH_SHORT)
                .show()
        } else {
            //The user has never accepted or declined, so we ask him to accept the permission.
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    presenter.onCameraButtonClicked()
                    //The user has accepted the permission, he doesn't have to click the button again, we can launch the functionality from here.
                } else {
                    Toast.makeText(this,
                        getString(R.string.msg_invalid_permission_msg),
                        Toast.LENGTH_SHORT).show()
                    //The user has refused the permission, we can disable the functionality or show a view/dialog.
                }
                return
            }
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                } else {
                    // Permiso denegado
                    Toast.makeText(this,
                        getString(R.string.msg_permission_denied),
                        Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                // We leave this else in case a permission comes out that we did not have controlled.
            }
        }
    }

    //show file
    override fun showFileChooser(intent: Intent) {
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)),
            FILE_SELECT_CODE)
    }

}