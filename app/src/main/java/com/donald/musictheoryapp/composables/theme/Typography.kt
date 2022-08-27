package com.donald.musictheoryapp.composables.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.donald.musictheoryapp.R

/*
val Typography = Typography(
    defaultFontFamily = FontFamily.Default,
    h1 = TextStyle(fontSize = 96.sp, fontWeight = FontWeight.Medium),
    h2 = TextStyle(fontSize = 60.sp, fontWeight = FontWeight.Medium),
    h3 = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Medium),
    h4 = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Medium),
    h5 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium),
    h6 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
    subtitle1 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    subtitle2 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    body1 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    body2 = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    caption = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    button = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    overline = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium)
)

 */

private val DefaultFontFamily = FontFamily(
    Font(R.font.varela_round_regular)
)

object StandardAlertDialogTypography {
    val title = TextStyle(
        fontSize = 20.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold
    )
    val description = TextStyle(
        fontSize = 16.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val button = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        letterSpacing = 2.sp
    )
}

object TextInputTypography {
    val label = TextStyle(
        fontSize = 14.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val textField = TextStyle(
        fontSize = 20.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
}

object PopupMenuTypography {
    val popupMenuTitle = TextStyle(
        fontSize = 14.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    )
    val popupMenuItem = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
    )
}

object ListItemTypography {
    val large = TextStyle(
        fontSize = 22.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
    )
    val medium = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
}

object ExerciseTypography {
    val textDescription = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val textEmphasizeDescription = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    val textButton = TextStyle(
        fontSize = 24.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
    )
    val questionInfoPanel = TextStyle(
        fontSize = 22.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val bottomBar = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val intervalInputTip = TextStyle(
        fontSize = 16.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
    )
    val imageLoadError = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val insufficientPoints = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
    )
    val paused = TextStyle(
        fontSize = 32.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    )
    val correctAnswerPanel = TextStyle(
        fontSize = 20.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val inputHint: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
}

object MainTypography {
    @Deprecated("Use dimens instead")
    val button = TextStyle(
        fontSize = 28.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp
    )
}

object StandardButtonTypography {
    val button = TextStyle(
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp
    )
}

object ProfileTypography {
    val nickname = TextStyle(
        fontSize = 28.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Bold
    )
    val points = TextStyle(
        fontSize = 22.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
    val error = TextStyle(
        fontSize = 18.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
}

object ExerciseHistoryTypography {
    val points = TextStyle(
        fontSize = 22.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
    )
    val confirmButton = TextStyle(
        fontSize = 28.sp,
        letterSpacing = 2.sp,
        fontFamily = DefaultFontFamily,
        fontWeight = FontWeight.Normal
    )
}

val Typography = Typography(
    defaultFontFamily = DefaultFontFamily
)