package com.zaidali.survey

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.zaidali.survey.databinding.ActivitySurveyBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SurveyActivity : BaseBindingActivity<ActivitySurveyBinding>(R.layout.activity_survey) {

    private var photoUri: Uri? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            }
        }
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            binding.imageView.setImageBitmap(bitmap)
            photoUri = bitmap?.let { bitmapToUri(this@SurveyActivity , it) }
        }

    /*private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val photoFile = File(externalCacheDir, "temp_photo.jpg")
                photoUri = Uri.fromFile(photoFile)
                binding.imageView.setImageURI(photoUri)
            }
        }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receivedData = intent.getStringExtra("NAME_APP")
        binding.title.text = receivedData

        binding.addImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                dispatchTakePictureIntent()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.save.setOnClickListener{
            if(validateInputs()){
                val uri = photoUri ?: ""
                val survey = Survey(binding.txtField1.text.toString(), binding.txtField2.text.toString() , uri.toString() )
                val resultIntent = Intent()
                val gson = Gson()
                val surveyString = gson.toJson(survey)
                resultIntent.putExtra("survey", surveyString)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        binding.txtField1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val value: String = s.toString()
                try {
                    valueChanged(binding.libField1 , value)
                }catch (e:Exception){}

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })

        binding.txtField2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val value: String = s.toString()
                try {
                    valueChanged(binding.libField2 , value)
                }catch (e:Exception){}

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        })


    }
    fun valueChanged(textInputLayout: TextInputLayout, value: String?) {
        if (value!!.isEmpty()) {
            //textInputLayout.error = "Enter Field 1"
        } else {
            textInputLayout.error = null
        }
    }

    private fun dispatchTakePictureIntent() {
        takePictureLauncher.launch(null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun validateInputs(): Boolean{
        var result = true
        if (binding.txtField1.text.toString().isEmpty()) {
            binding.libField1.error = "Enter Field 1"
            result= false
        }
        if (binding.txtField2.text.toString().isEmpty()) {
            binding.libField2.error = "Enter Field 2"
            result= false
        }
        return result
    }
    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        // Create a temporary file to store the image
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File? = context.getExternalFilesDir(null)
        val imageFile = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )

        try {
            // Save the bitmap to the file
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        // Return the URI of the saved image
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

}