package com.zaidali.survey

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Survey(
    var field1: String = "",
    var field2: String = "",
    var image: String = "",
) : Parcelable
