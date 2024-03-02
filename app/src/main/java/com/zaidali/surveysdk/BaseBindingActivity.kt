package com.zaidali.surveysdk

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


abstract class BaseBindingActivity<T : ViewDataBinding>(
    @LayoutRes private val contentLayoutId: Int
) : AppCompatActivity() {

    private lateinit var _binding: T
    val binding: T get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = DataBindingUtil.setContentView<T>(this@BaseBindingActivity, contentLayoutId)
        _binding.executePendingBindings()
        _binding.lifecycleOwner = this
    }

}