package com.app.grader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.grader.R

val QuickSandsFont = FontFamily(
    Font(R.font.quicksand_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.quicksand_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.quicksand_light, FontWeight.Light, FontStyle.Normal),
)

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 22.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = QuickSandsFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 16.sp,
    )
)