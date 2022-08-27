package com.donald.musictheoryapp.composables.activitycomposables.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.donald.musictheoryapp.R
import com.donald.musictheoryapp.composables.general.StandardTextButton
import com.donald.musictheoryapp.composables.theme.CustomTheme
import com.donald.musictheoryapp.composables.theme.profileDimens
import com.donald.musictheoryapp.composables.theme.profileTypography
import com.donald.musictheoryapp.util.Profile
import java.text.NumberFormat
import java.util.*

@Preview
@Composable
private fun Preview() = CustomTheme {
}

@Composable
private fun ProfileColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) = Column(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier,
    content = content
)

@Composable
fun ReadyProfilePanel(
    profilePicture: Painter,
    profile: Profile,
    modifier: Modifier = Modifier
) {
    ProfileColumn(modifier = modifier) {
        Image(
            painter = profilePicture,
            contentDescription = null,
            modifier = Modifier
                .size(MaterialTheme.profileDimens.profilePicture)
                .clip(CircleShape)
        )
        Text(
            text = profile.nickname,
            style = MaterialTheme.profileTypography.nickname,
            color = MaterialTheme.colors.onSurface
        )
        val formattedPoints = NumberFormat.getInstance(Locale.getDefault()).format(profile.points)
        Text(
            text = stringResource(R.string.profile_points, formattedPoints),
            style = MaterialTheme.profileTypography.points,
            color = MaterialTheme.colors.onSurface
        )
    }
}