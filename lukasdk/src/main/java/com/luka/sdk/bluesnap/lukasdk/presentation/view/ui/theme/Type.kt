package com.luka.sdk.bluesnap.lukasdk.presentation.view.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luka.sdk.bluesnap.lukasdk.R

val openSansFamily = FontFamily(
    Font(R.font.opensans_bold, FontWeight.Bold),
    Font(R.font.opensans_semibold, FontWeight.SemiBold),
    Font(R.font.opensans_regular, FontWeight.Normal),
    Font(R.font.opensans_light, FontWeight.Light)
)


val Typography = Typography(
    titleLarge = TextStyle(fontFamily = openSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = openSansFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = openSansFamily,fontWeight = FontWeight.Normal,  fontSize = 16.sp),
    labelSmall = TextStyle(fontSize = 12.sp,fontWeight = FontWeight.Normal, fontFamily = openSansFamily)
)