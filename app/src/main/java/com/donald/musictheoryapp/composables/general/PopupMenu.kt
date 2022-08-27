package com.donald.musictheoryapp.composables.general

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.elevation
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.popupMenuTypography
import com.donald.musictheoryapp.composables.theme.popupMenuColors
import com.donald.musictheoryapp.composables.theme.popupMenuDimens

//private val MenuItemHeight = 40.dp

@Preview(showBackground = true)
@Composable
private fun PopupMenuPreview() = CustomTheme {
    val menu = menu(
        page(
            title = { PopupMenuTitleText(text = "Menu") },
            item(
                { Text(text = "Item A") },
                page(
                    title = { Text("Are you sure?") },
                    item(
                        { Text(text = "Ok") },
                        { Log.d("ContextMenu", "Ok detected") }
                    ),
                    item(
                        { Text(text = "Cancel") },
                        { Log.d("ContextMenu", "Cancel detected") }
                    ),
                    item(
                        { Text(text = "Retry") },
                        {}
                    )
                )
            ),
            item(
                { Text(text = "Item B") },
                page(
                    title = { Text("Please confirm.") },
                    item(
                        { Text(text = "Confirm") },
                        { Log.d("ContextMenu", "Confirm detected") }
                    ),
                    item(
                        { Text(text = "Abort") },
                        { Log.d("ContextMenu", "Abort detected") }
                    )
                )
            ),
            item(
                { Text(text = "Item C") },
                {}
            )
        )
    )
    PopupMenu(
        menu = menu,
        //onFinalItemClick = { it.invoke() },
        rowHeight = 40.dp,
        //state = PopupMenuState.Disabled,
        state = PopupMenuState.Disabled,
        modifier = Modifier.width(200.dp)
    )
}

@Composable
fun PopupMenuItemText(
    text: String,
    modifier: Modifier = Modifier
) = Text(
    text = text,
    style = MaterialTheme.popupMenuTypography.popupMenuItem,
    color = MaterialTheme.colors.onSurface,
    modifier = modifier
)

@Composable
fun PopupMenuTitleText(
    text: String,
    modifier: Modifier = Modifier
) = Text(
    text = text,
    style = MaterialTheme.popupMenuTypography.popupMenuTitle,
    color = MaterialTheme.popupMenuColors.title,
    modifier = modifier
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> PopupMenu(
    menu: Menu<T>,
    rowHeight: Dp,
    //onFinalItemClick: (T) -> Unit,
    state: PopupMenuState<T>,
    modifier: Modifier = Modifier,
) {
    // nothing to draw if the menu is empty
    if (menu.pages.isEmpty()) return

    // a stack for backward navigation
    val pageStack = remember { ArrayDeque<MenuPage<T>>() }

    // mutable state to trigger recomposition
    var displayData by remember {
        val firstPage = menu.pages[0]
        pageStack.addLast(firstPage)
        mutableStateOf(DisplayData(firstPage, TransitionDirection.Forward, showsBackButton = false))
    }

    //val height = rowHeight * displayData.page.items.size + if (true || displayData.page.title != null) rowHeight else 0.dp
    val itemColumnState = when (state) {
        is PopupMenuState.Enabled<T> -> ItemColumnState.Enabled(
            onForward = { item: ForwardMenuItem<T> ->
                val nextPage = item.nextPage
                pageStack.addLast(nextPage)
                displayData = DisplayData(nextPage, TransitionDirection.Forward, showsBackButton = true)
            },
            onBackward = {
                pageStack.removeLast()
                displayData = DisplayData(
                    page = pageStack.last(),
                    direction = TransitionDirection.Backward,
                    showsBackButton = pageStack.size != 1
                )
            },
            onFinalItemClicked = state.onFinalItemClicked
        )
        PopupMenuState.Disabled -> ItemColumnState.Disabled
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colors.surface,
        elevation = elevation(1),
        modifier = modifier
            .wrapContentHeight()
    ) {
        AnimatedContent(
            transitionSpec = {
                when (displayData.direction) {
                    TransitionDirection.Forward -> slideInHorizontally(
                        initialOffsetX = { width -> width }
                    ) with slideOutHorizontally(
                        targetOffsetX = { width -> -width }
                    )
                    TransitionDirection.Backward -> slideInHorizontally(
                        initialOffsetX = { width -> -width }
                    ) with slideOutHorizontally(
                        targetOffsetX = { width -> width }
                    )
                }
            },
            targetState = displayData,
            modifier = Modifier.fillMaxWidth()
        ) { targetDisplayData ->
            ItemColumn(targetDisplayData, rowHeight, itemColumnState)
        }
    }
}

@Composable
private fun <T> ItemColumn(
    displayData: DisplayData<T>,
    rowHeight: Dp,
    //onFinalItemClicked: (T) -> Unit
    state: ItemColumnState<T>
) {
    val (page, _, showsBackwardButton) = displayData
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. title row
        if (true || showsBackwardButton || page.title != null) MenuTitle(
            title = page.title,
            showsBackwardButton = true || showsBackwardButton,
            rowHeight = rowHeight,
            state = when (state) {
                is ItemColumnState.Enabled<T> -> {
                   ClickableState.Enabled(state.onBackward)
                }
                ItemColumnState.Disabled -> {
                    ClickableState.Disabled
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        )
        // 2. items
        for (item in page.items) {
            val showsForwardButton: Boolean
            val clickableState: ClickableState
            //val onClick: () -> Unit
            when (item) {
                is ForwardMenuItem<T> -> {
                    showsForwardButton = true
                    clickableState = when (state) {
                        is ItemColumnState.Enabled<T> -> {
                            ClickableState.Enabled { state.onForward(item) }
                        }
                        ItemColumnState.Disabled -> {
                            ClickableState.Disabled
                        }
                    }
                }
                is FinalMenuItem<T> -> {
                    showsForwardButton = false
                    clickableState = when (state) {
                        is ItemColumnState.Enabled<T> -> {
                            ClickableState.Enabled { state.onFinalItemClicked(item.value) }
                        }
                        ItemColumnState.Disabled -> {
                            ClickableState.Disabled
                        }
                    }
                }
            }
            MenuItem(
                item = item,
                showsForwardButton = showsForwardButton,
                state = clickableState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    //.padding(16.dp)
            )
        }
    }
}

@Composable
private fun MenuTitle(
    title: @Composable (() -> Unit)?,
    showsBackwardButton: Boolean,
    rowHeight: Dp,
    //onBackward: () -> Unit,
    state: ClickableState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = run {
            val boxModifier = modifier
                .height(rowHeight)
                .fillMaxWidth()
            if (showsBackwardButton && state is ClickableState.Enabled) boxModifier.then(
                Modifier.clickable(onClick = state.onClick)
            ) else boxModifier
        }
    ) {
        if (showsBackwardButton) Image(
            painter = painterResource(R.drawable.ic_popup_menu_back_button),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.popupMenuColors.title),
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .padding(start = MaterialTheme.popupMenuDimens.popupMenuHorizontalPadding)
                .align(Alignment.CenterStart)
        )
        if (title != null) Box(modifier = Modifier.align(Alignment.Center)) {
            title.invoke()
        }
    }
}

@Composable
private fun <T> MenuItem(
    item: MenuItem<T>,
    showsForwardButton: Boolean,
    state: ClickableState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .then(
                when (state) {
                    is ClickableState.Enabled -> Modifier.clickable(onClick = state.onClick)
                    ClickableState.Disabled -> Modifier
                }
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = MaterialTheme.popupMenuDimens.popupMenuHorizontalPadding)
        ) {
            item.content()
        }

        if (showsForwardButton) Image(
            painter = painterResource(R.drawable.ic_popup_menu_forward_button),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            modifier = Modifier
                .fillMaxHeight(0.5F)
                .padding(end = MaterialTheme.popupMenuDimens.popupMenuHorizontalPadding)
                .align(Alignment.CenterEnd)
        )
    }
}

class Menu<T>(val pages: List<MenuPage<T>>)

class MenuPage<T>(
    val title: (@Composable () -> Unit)?,
    val items: List<MenuItem<T>>
)

sealed class MenuItem<T>(val content: @Composable () -> Unit)

class BackwardMenuItem<T>(
    content: @Composable () -> Unit
)

class ForwardMenuItem<T>(
    content: @Composable () -> Unit,
    val nextPage: MenuPage<T>
) : MenuItem<T>(content)

class FinalMenuItem<T>(
    content: @Composable () -> Unit,
    val value: T
) : MenuItem<T>(content)

fun <T> menu(vararg pages: MenuPage<T>) = Menu(pages.toList())

fun <T> page(title: (@Composable () -> Unit)?, vararg items: MenuItem<T>) = MenuPage(title, items.toList())

fun <T> item(content: @Composable () -> Unit, page: MenuPage<T>): ForwardMenuItem<T> {
    return ForwardMenuItem(content, page)
}

fun <T> item(content: @Composable () -> Unit, value: T): FinalMenuItem<T> {
    return FinalMenuItem(content, value)
}

private enum class TransitionDirection { Forward, Backward }

private data class DisplayData<T>(val page: MenuPage<T>, val direction: TransitionDirection, val showsBackButton: Boolean)

sealed class PopupMenuState<out T> {
    class Enabled<T>(
        val onFinalItemClicked: (T) -> Unit,
        //val onForward: (ForwardMenuItem<T>) -> Unit,
        //val onBackward: () -> Unit,
    ) : PopupMenuState<T>()
    object Disabled : PopupMenuState<Nothing>()
}

private sealed class ItemColumnState<out T> {
    class Enabled<T>(
        val onFinalItemClicked: (T) -> Unit,
        val onForward: (ForwardMenuItem<T>) -> Unit,
        val onBackward: () -> Unit,
    ) : ItemColumnState<T>()
    object Disabled : ItemColumnState<Nothing>()
}

private sealed class ClickableState {
    class Enabled(val onClick: () -> Unit) : ClickableState()
    object Disabled : ClickableState()
}