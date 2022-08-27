package com.donald.musictheoryapp.composables.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF_B6_FF_FF)
private val PrimaryVariant = Color(0xFF_81_D4_FA)
private val OnPrimary = Color(0xFF_3D_3D_3D)

private val Secondary = Color(0xFF_FF_C1_E3)
private val SecondaryVariant = Color(0xFF_F4_8F_B1)
private val OnSecondary = Color(0xFF_3D_3D_3D)

private val Background =  Color(0xFF_25_25_25)
private val OnBackground = Color(0xFF_FF_FF_FF)

private val Surface = Color(0xFF_3D_3D_3D)
private val OnSurface = Color(0xFF_FF_FF_FF)

private val Error = Color(0xFF_F7_78_50)
private val OnError = Color(0xFF_3D_3D_3D)

val DarkColorPalette = darkColors(
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