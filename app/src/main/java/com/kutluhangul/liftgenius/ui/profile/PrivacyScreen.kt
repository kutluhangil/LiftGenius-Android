package com.kutluhangul.liftgenius.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.SubPageScaffold
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun PrivacyScreen(onClose: () -> Unit) {
    val sections = listOf(
        stringResource(R.string.privacy_s1_title) to stringResource(R.string.privacy_s1_body),
        stringResource(R.string.privacy_s2_title) to stringResource(R.string.privacy_s2_body),
        stringResource(R.string.privacy_s3_title) to stringResource(R.string.privacy_s3_body),
        stringResource(R.string.privacy_s4_title) to stringResource(R.string.privacy_s4_body),
        stringResource(R.string.privacy_s5_title) to stringResource(R.string.privacy_s5_body),
    )

    SubPageScaffold(title = stringResource(R.string.privacy_title), onClose = onClose) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(36.dp),
                )
            }
            item {
                Column {
                    Text(stringResource(R.string.privacy_title), style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = stringResource(R.string.privacy_intro),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extended.textSecondary,
                    )
                }
            }
            items(sections.size) { index ->
                val (title, body) = sections[index]
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.extended.textSecondary,
                    )
                }
            }
        }
    }
}
