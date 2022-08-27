package com.donald.musictheoryapp.composables.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalColors = staticCompositionLocalOf { MoreColorPaletteLight }
private val LocalPopupMenuColors = staticCompositionLocalOf { PopupMenuColors }
private val LocalExerciseColors = staticCompositionLocalOf { ButtonInputColors }
private val LocalTextFieldColors = staticCompositionLocalOf { TextInputColors }

private val LocalDimens = staticCompositionLocalOf { Dimens }
private val LocalPopupMenuDimens = staticCompositionLocalOf { PopupMenuDimens }
private val LocalListItemDimens = staticCompositionLocalOf { ListItemDimens }
private val LocalAppBarDimens = staticCompositionLocalOf { AppBarDimens }
private val LocalExerciseDimens = staticCompositionLocalOf { ExerciseDimens }
private val LocalStandardButtonDimens = staticCompositionLocalOf { StandardButtonDimens }
private val LocalProfileDimens = staticCompositionLocalOf { ProfileDimens }
private val LocalMainDimens = staticCompositionLocalOf { MainDimens }
private val LocalStandardAlertDialogDimens = staticCompositionLocalOf { StandardAlertDialogDimens }

private val LocalStandardAlertDialogTypography = staticCompositionLocalOf { StandardAlertDialogTypography }
private val LocalTextFieldTypography = staticCompositionLocalOf { TextInputTypography }
private val LocalPopupTypography = staticCompositionLocalOf { PopupMenuTypography }
private val LocalListItemTypography = staticCompositionLocalOf { ListItemTypography }
private val LocalExerciseTypography = staticCompositionLocalOf { ExerciseTypography }
private val LocalMainTypography = staticCompositionLocalOf { MainTypography }
private val LocalStandardButtonTypography = staticCompositionLocalOf { StandardButtonTypography }
private val LocalProfileTypography = staticCompositionLocalOf { ProfileTypography }
private val LocalExerciseHistoryTypography = staticCompositionLocalOf { ExerciseHistoryTypography }

private val LocalShapes = staticCompositionLocalOf { MoreShapes }

// TODO: IMPLEMENT DARK THEME
@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val materialColorPalette = /*if (darkTheme) DarkColorPalette else*/ MaterialColorPaletteLight
    CompositionLocalProvider(
        LocalColors provides MoreColorPaletteLight,
        LocalPopupMenuColors provides PopupMenuColors,
        LocalExerciseColors provides ButtonInputColors,
        LocalTextFieldColors provides TextInputColors,

        LocalDimens provides Dimens,
        LocalPopupMenuDimens provides PopupMenuDimens,
        LocalListItemDimens provides ListItemDimens,
        LocalAppBarDimens provides AppBarDimens,
        LocalExerciseDimens provides ExerciseDimens,
        LocalStandardButtonDimens provides StandardButtonDimens,
        LocalProfileDimens provides ProfileDimens,
        LocalMainDimens provides MainDimens,
        LocalStandardAlertDialogDimens provides StandardAlertDialogDimens,

        LocalStandardAlertDialogTypography provides StandardAlertDialogTypography,
        LocalTextFieldTypography provides TextInputTypography,
        LocalPopupTypography provides PopupMenuTypography,
        LocalListItemTypography provides ListItemTypography,
        LocalExerciseTypography provides ExerciseTypography,
        LocalMainTypography provides MainTypography,
        LocalStandardButtonTypography provides StandardButtonTypography,
        LocalProfileTypography provides ProfileTypography,
        LocalExerciseHistoryTypography provides ExerciseHistoryTypography,

        LocalShapes provides MoreShapes,
    ) {
        MaterialTheme(
            colors = materialColorPalette,
            shapes = Shapes,
            typography = Typography,
            content = content
        )
    }
}

val MaterialTheme.moreColors: MoreColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

val MaterialTheme.popupMenuColors: PopupMenuColors
    @Composable
    @ReadOnlyComposable
    get() = LocalPopupMenuColors.current

val MaterialTheme.buttonInputColors: ButtonInputColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExerciseColors.current

val MaterialTheme.textInputColors: TextInputColors
    @Composable
    @ReadOnlyComposable
    get() = LocalTextFieldColors.current

val MaterialTheme.dimens: Dimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDimens.current

val MaterialTheme.popupMenuDimens: PopupMenuDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalPopupMenuDimens.current

val MaterialTheme.listItemDimens: ListItemDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalListItemDimens.current

val MaterialTheme.appBarDimens: AppBarDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalAppBarDimens.current

val MaterialTheme.exerciseDimens: ExerciseDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalExerciseDimens.current

val MaterialTheme.standardButtonDimens: StandardButtonDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalStandardButtonDimens.current

val MaterialTheme.profileDimens: ProfileDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalProfileDimens.current

val MaterialTheme.mainDimens: MainDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalMainDimens.current

val MaterialTheme.standardAlertDialogDimens: StandardAlertDialogDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalStandardAlertDialogDimens.current

val MaterialTheme.standardAlertDialogTypography: StandardAlertDialogTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalStandardAlertDialogTypography.current

val MaterialTheme.textInputTypography: TextInputTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalTextFieldTypography.current

val MaterialTheme.popupMenuTypography: PopupMenuTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalPopupTypography.current

val MaterialTheme.listItemTypography: ListItemTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalListItemTypography.current

val MaterialTheme.exerciseTypography: ExerciseTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalExerciseTypography.current

val MaterialTheme.mainTypography: MainTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalMainTypography.current

val MaterialTheme.standardButtonTypography: StandardButtonTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalStandardButtonTypography.current

val MaterialTheme.profileTypography: ProfileTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalProfileTypography.current

val MaterialTheme.exerciseHistoryTypography: ExerciseHistoryTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalExerciseHistoryTypography.current

val MaterialTheme.moreShapes: MoreShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalShapes.current