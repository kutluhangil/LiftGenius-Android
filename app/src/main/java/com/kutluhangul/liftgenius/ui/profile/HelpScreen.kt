package com.kutluhangul.liftgenius.ui.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.SettingsDivider
import com.kutluhangul.liftgenius.ui.components.SubPageScaffold
import com.kutluhangul.liftgenius.ui.components.sendSupportEmail
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun HelpScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val email = stringResource(R.string.support_email)
    val faqs = listOf(
        stringResource(R.string.faq_q1) to stringResource(R.string.faq_a1),
        stringResource(R.string.faq_q2) to stringResource(R.string.faq_a2),
        stringResource(R.string.faq_q3) to stringResource(R.string.faq_a3),
        stringResource(R.string.faq_q4) to stringResource(R.string.faq_a4),
        stringResource(R.string.faq_q5) to stringResource(R.string.faq_a5),
    )

    SubPageScaffold(title = stringResource(R.string.help_title), onClose = onClose) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.lg),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.md),
        ) {
            item {
                Icon(
                    imageVector = Icons.Filled.SupportAgent,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(36.dp),
                )
            }
            item { Text(stringResource(R.string.help_headline), style = MaterialTheme.typography.headlineSmall) }
            item {
                Text(
                    text = stringResource(R.string.help_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extended.textSecondary,
                )
            }
            item {
                Text(
                    text = stringResource(R.string.help_faq),
                    style = KickerLabel,
                    color = MaterialTheme.extended.textTertiary,
                )
            }
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    faqs.forEachIndexed { index, (q, a) ->
                        FaqItem(q, a)
                        if (index < faqs.lastIndex) SettingsDivider()
                    }
                }
            }
            item {
                GradientButton(
                    text = stringResource(R.string.help_email_support),
                    onClick = { sendSupportEmail(context, email, "LiftGenius — Destek") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize()
            .padding(vertical = Spacing.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(question, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.extended.textTertiary,
            )
        }
        if (expanded) {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extended.textSecondary,
            )
        }
    }
}
