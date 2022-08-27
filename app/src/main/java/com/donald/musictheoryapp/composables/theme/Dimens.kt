package com.donald.musictheoryapp.composables.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// THIS REALLY DOES NOT NEED TO BE PART OF THE THEME AND CAN BE ITS OWN CLASS

object Dimens {
    val surfaceBorderStrokeWidth: Dp = 2.dp
    val dividerThickness: Dp = 2.dp
    val elevationStep: Dp = 4.dp
}

object PopupMenuDimens {
    val popupMenuRowHeight = 50.dp
    val popupMenuHorizontalPadding = 12.dp
    val popupMenuDividerPadding = 18.dp
}

object ListItemDimens {
    val colorBarWidth = 8.dp
    val innerHPadding = 20.dp
    val innerVPadding = 16.dp
}

object AppBarDimens {
    val clickableRippleRadius = 20.dp
}

object ExerciseDimens {
    val textEmphasizeInnerPadding = 6.dp
    val checkBoxBorderThickness = 4.dp
    val imageButtonIndexLabelPadding = 8.dp
}

object StandardButtonDimens {
    val defaultFontSize = 14.sp
    val defaultPadding = 12.dp
}

object ProfileDimens {
    val profilePicture = 128.dp
}

object MainDimens {
    val buttonFontSize = 28.sp
}

object StandardAlertDialogDimens {
    val buttonRippleRadius = 20.dp
}