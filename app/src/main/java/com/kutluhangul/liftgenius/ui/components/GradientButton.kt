package com.kutluhangul.liftgenius.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.OnAccent

/** Primary CTA: brand gradient fill, 52dp height (≥48dp touch target), built-in loading state. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.height(52.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (enabled) 1f else 0.5f)
                .background(BrandGradient),
            contentAlignment = Alignment.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = OnAccent,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = OnAccent,
                )
            }
        }
    }
}
