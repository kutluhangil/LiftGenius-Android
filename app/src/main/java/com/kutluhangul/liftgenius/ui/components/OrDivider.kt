package com.kutluhangul.liftgenius.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

/** "— veya —" separator between primary and alternative auth actions. */
@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.extended.ledgerDivider,
        )
        Text(
            text = stringResource(R.string.auth_or),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.extended.textTertiary,
            modifier = Modifier.padding(horizontal = Spacing.md),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.extended.ledgerDivider,
        )
    }
}
