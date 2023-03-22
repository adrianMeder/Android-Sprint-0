package com.example.documentos.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher

interface IDocument {
    interface View {
        fun showPhoto(photo: Bitmap)
        fun showErrorMessage(message: String)
        fun showGallerySelection()
        fun showCamera()
        fun showFileChooser(intent: Intent)
    }
    interface Presenter {
        fun permissionStorage()
        fun onButtonClicked(selectPDFFile: ActivityResultLauncher<String>)
        fun onFileSelected(fileUri: Uri)
    }

    interface Model {}
}