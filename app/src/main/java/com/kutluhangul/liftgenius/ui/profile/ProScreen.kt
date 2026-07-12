package com.kutluhangul.liftgenius.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.ui.components.GlassCard
import com.kutluhangul.liftgenius.ui.components.GradientButton
import com.kutluhangul.liftgenius.ui.components.SubPageScaffold
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

private data class ProFeature(val labelRes: Int, val freeText: String?, val freeCheck: Boolean, val proText: String?)

@Composable
fun ProScreen(onClose: () -> Unit) {
    SubPageScaffold(title = stringResource(R.string.pro_title), onClose = onClose) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.lg))
            Text(
                text = "PRO",
                style = MaterialTheme.typography.displayLarge.merge(TextStyle(brush = BrandGradient)),
                fontWeight = FontWeight.Black,
            )
            Spacer(Modifier.height(Spacing.sm))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), RoundedCornerShape(50))
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            ) {
                Text(
                    text = stringResource(R.string.pro_coming_soon),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(Spacing.xl))
            Text(stringResource(R.string.pro_headline), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(R.string.pro_price),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(Spacing.xl))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row {
                    Spacer(Modifier.weight(1.6f))
                    Text(stringResource(R.string.pro_free), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.extended.textSecondary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(stringResource(R.string.pro_pro), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(Spacing.md))
                FeatureRow(stringResource(R.string.pro_feature_clients), free = stringResource(R.string.pro_free_clients), pro = stringResource(R.string.pro_pro_clients))
                FeatureRow(stringResource(R.string.pro_feature_ai), freeCheck = false, proCheck = true)
                FeatureRow(stringResource(R.string.pro_feature_programs), freeCheck = true, proCheck = true)
                FeatureRow(stringResource(R.string.pro_feature_pdf), freeCheck = true, proCheck = true)
                FeatureRow(stringResource(R.string.pro_feature_reminders), freeCheck = true, proCheck = true)
                FeatureRow(stringResource(R.string.pro_feature_reports), freeCheck = true, proCheck = true)
            }
            Spacer(Modifier.height(Spacing.xl))
            GradientButton(
                text = stringResource(R.string.pro_cta),
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = stringResource(R.string.pro_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extended.textTertiary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun FeatureRow(
    label: String,
    free: String? = null,
    pro: String? = null,
    freeCheck: Boolean? = null,
    proCheck: Boolean? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1.6f))
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { Cell(free, freeCheck, isPro = false) }
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { Cell(pro, proCheck, isPro = true) }
    }
}

@Composable
private fun Cell(text: String?, check: Boolean?, isPro: Boolean) {
    when {
        text != null -> Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.extended.textSecondary,
            fontWeight = if (isPro) FontWeight.SemiBold else FontWeight.Normal,
        )
        check == true -> Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.extended.success, modifier = Modifier.size(22.dp))
        check == false -> Icon(Icons.Filled.Cancel, contentDescription = null, tint = MaterialTheme.extended.textTertiary, modifier = Modifier.size(22.dp))
    }
}
