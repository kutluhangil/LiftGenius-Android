package com.kutluhangul.liftgenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

/** Gradient wordmark with kicker and accent bar — shared by splash and welcome. */
@Composable
fun BrandWordmark(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.brand_kicker),
            style = KickerLabel,
            color = MaterialTheme.extended.textTertiary,
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = "LiftGenius",
            style = MaterialTheme.typography.displayLarge.merge(TextStyle(brush = BrandGradient)),
        )
        Spacer(Modifier.height(Spacing.lg))
        Box(
            Modifier
                .width(56.dp)
                .height(4.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(BrandGradient),
        )
    }
}
