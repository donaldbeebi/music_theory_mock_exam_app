package com.donald.musictheoryapp.composables.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF_20_A5_E2)
private val PrimaryVariant = Color(0xFF_0A_77_CA)
private val OnPrimary = Color(0xFF_FF_FF_FF)

private val Secondary = Color(0xFF_E4_59_87)
private val SecondaryVariant = Color(0xFF_CE_30_6A)
private val OnSecondary = Color(0xFF_FF_FF_FF)

private val Background = Color(0xFFE7E7E7)
private val OnBackground = Color(0xFFA2A2A2)

private val Surface = Color(0xFF_FF_FF_FF)
private val OnSurface = Color(0xFF_3C_3C_3C)

private val Error = Color(0xFF_FA_5C_2A)
private val OnError = Color(0xFF_FF_FF_FF)

// EXTRA COLORS
private val SurfaceBorder = Color(0xFFDFDFDF)

private val ThinDivider = Color(0xFF_E9_E9_E9)

private val ThickDivider = Color(0xFF_97_97_97)
private val OnThickDivider = Color(0xFF_FF_FF_FF)

private val Disabled = Color(0xFFCECECE)
private val OnDisabled = Color(0xFF_FF_FF_FF)

private val FocusBackdrop = Color(0x28000000)

val MaterialColorPaletteLight = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    onPrimary = OnPrimary,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    error = Error,
    onError = OnError
)

val MoreColorPaletteLight = MoreColors(
    surfaceBorder = SurfaceBorder,
    thinDivider = ThinDivider,
    thickDivider = ThickDivider,
    onThickDivider = OnThickDivider,
    disabled = Disabled,
    onDisabled = OnDisabled,
    focusBackdrop = FocusBackdrop
)

object PopupMenuColors {
    val title: Color = Color(0xFF_B3_B3_B3)
}

object ButtonInputColors {
    val buttonTopFaceDefault: Color = Primary
    val buttonFrontFaceDefault: Color = Color(0xFF_1E_6F_94)

    val buttonTopFaceSelected: Color = Secondary
    val buttonFrontFaceSelected: Color = Color(0xFF_8F_3F_59)

    val imageButtonIndexLabelBackground = Color(0x6D_00_00_00)
    val imageButtonIndexLabelText = Color(0xFF_FF_FF_FF)
}

object TextInputColors {
    val inputText = Color(0xFF_3C_3C_3C)
    val unfocused = Color(0xFF_81_81_81)
    val background = Color(0xFFCECECE)
}