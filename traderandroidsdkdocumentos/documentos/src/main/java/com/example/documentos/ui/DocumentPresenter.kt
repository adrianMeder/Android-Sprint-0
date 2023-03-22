package com.example.documentos.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.documentos.R
import java.io.File

class DocumentPresenter(
    private val view: IDocument.View,
    private val activity: Activity,
    private val context: Context,
) : IDocument.Presenter {

    private val model=DocumentModel(this,context)

    fun onGalleryButtonClicked() {
        view.showGallerySelection()
    }

    fun onCameraButtonClicked() {
        view.showCamera()
    }

    fun onPhotoTaken(photo: Bitmap) {
        view.showPhoto(photo)
    }

    fun onError(errorMessage: String) {
        view.showErrorMessage(errorMessage)
    }

    //Open file
    override fun onButtonClicked(selectPDFFile: ActivityResultLauncher<String>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        selectPDFFile.launch(intent.type)
    }

  override fun permissionStorage(){

      val permission = Manifest.permission.READ_EXTERNAL_STORAGE
      val grant = ContextCompat.checkSelfPermission(context, permission)
      if (grant != PackageManager.PERMISSION_GRANTED) {
          val permissionList = arrayOf(permission)
          ActivityCompat.requestPermissions(activity, permissionList, 1)
      }

    }

    override fun onFileSelected(fileUri: Uri) {
    }
}