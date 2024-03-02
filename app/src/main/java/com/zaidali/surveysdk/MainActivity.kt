package com.zaidali.surveysdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.zaidali.survey.Survey
import com.zaidali.survey.SurveyActivity
import com.zaidali.surveysdk.databinding.ActivityMainBinding

class MainActivity : BaseBindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var survey : Survey
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val surveyString = data?.getStringExtra("survey")
                survey = Gson().fromJson(surveyString, Survey::class.java)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.openSurvey.setOnClickListener {
            val intent = Intent(this, SurveyActivity::class.java)
            intent.putExtra("NAME_APP", "Hi App Survey")
            activityResultLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if(::survey.isInitialized) {
            survey?.let {
                binding.txtField1.text = it.field1
                binding.txtField2.text = it.field2
                if (it.image.isNotEmpty()) {
                    binding.lblImage.isVisible = true
                    binding.image.isVisible = true
                    binding.image.setImageURI(it.image.toUri())
                }else{
                    binding.lblImage.isVisible = false
                    binding.image.isVisible = false
                }

            }
        }


    }
}