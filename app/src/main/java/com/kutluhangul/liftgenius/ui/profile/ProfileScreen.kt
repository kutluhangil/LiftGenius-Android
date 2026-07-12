package com.kutluhangul.liftgenius.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale
import com.kutluhangul.liftgenius.R
import com.kutluhangul.liftgenius.domain.model.TrainerProfile
import com.kutluhangul.liftgenius.domain.model.TrainerRole
import com.kutluhangul.liftgenius.ui.common.label
import com.kutluhangul.liftgenius.ui.components.ErrorState
import com.kutluhangul.liftgenius.ui.components.InitialsAvatar
import com.kutluhangul.liftgenius.ui.components.LoadingState
import com.kutluhangul.liftgenius.ui.components.SettingsDivider
import com.kutluhangul.liftgenius.ui.components.SettingsGroup
import com.kutluhangul.liftgenius.ui.components.SettingsRow
import com.kutluhangul.liftgenius.ui.theme.BrandGradient
import com.kutluhangul.liftgenius.ui.theme.KickerLabel
import com.kutluhangul.liftgenius.ui.theme.OnAccent
import com.kutluhangul.liftgenius.ui.theme.Spacing
import com.kutluhangul.liftgenius.ui.theme.extended

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onOpenTeam: () -> Unit,
    onOpenPro: () -> Unit,
    onFeedback: () -> Unit,
    onHelp: () -> Unit,
    onPrivacy: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val notifications by viewModel.notificationsEnabled.collectAsState()

    when {
        uiState.isLoading -> LoadingState()
        uiState.profile == null && uiState.error != null -> ErrorState(
            message = uiState.error ?: stringResource(R.string.state_error_generic),
            onRetry = viewModel::load,
        )
        else -> ProfileContent(
            uiState = uiState,
            isDark = isDark,
            notifications = notifications,
            onEditProfile = onEditProfile,
            onOpenTeam = onOpenTeam,
            onOpenPro = onOpenPro,
            onFeedback = onFeedback,
            onHelp = onHelp,
            onPrivacy = onPrivacy,
            onToggleDark = viewModel::setDarkMode,
            onToggleNotifications = viewModel::setNotifications,
            onSignOut = viewModel::signOut,
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileViewModel.UiState,
    isDark: Boolean,
    notifications: Boolean,
    onEditProfile: () -> Unit,
    onOpenTeam: () -> Unit,
    onOpenPro: () -> Unit,
    onFeedback: () -> Unit,
    onHelp: () -> Unit,
    onPrivacy: () -> Unit,
    onToggleDark: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onSignOut: () -> Unit,
) {
    val profile = uiState.profile ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
    ) {
        Text(
            text = stringResource(R.string.tab_profile),
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(Spacing.xl))
        ProfileHeader(profile, uiState.email)
        Spacer(Modifier.height(Spacing.xxl))

        Column(
            modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
        ) {
            SectionKicker(stringResource(R.string.profile_account))
            SettingsGroup {
                SettingsRow(Icons.Filled.Person, stringResource(R.string.profile_personal_info), onClick = onEditProfile)
                SettingsDivider()
                SettingsRow(Icons.Filled.Business, stringResource(R.string.profile_business), tint = MaterialTheme.colorScheme.secondary, onClick = onEditProfile)
                SettingsDivider()
                SettingsRow(Icons.Filled.Groups, stringResource(R.string.profile_team), onClick = onOpenTeam)
                SettingsDivider()
                SettingsRow(Icons.Filled.WorkspacePremium, stringResource(R.string.profile_upgrade), tint = MaterialTheme.extended.warning, onClick = onOpenPro)
            }
            Spacer(Modifier.height(Spacing.lg))

            SectionKicker(stringResource(R.string.profile_preferences))
            SettingsGroup {
                val context = LocalContext.current
                val isEnglish = Locale.getDefault().language == "en"
                SettingsRow(
                    Icons.Filled.Language,
                    stringResource(R.string.profile_language),
                    showChevron = false,
                    onClick = {
                        val target = if (isEnglish) "tr" else "en"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.getSystemService(LocaleManager::class.java)
                                .applicationLocales = LocaleList.forLanguageTags(target)
                        } else {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(target),
                            )
                        }
                    },
                    trailing = {
                        Text(
                            text = if (isEnglish) "English" else stringResource(R.string.lang_turkish),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.extended.textSecondary,
                        )
                    },
                )
                SettingsDivider()
                SettingsRow(
                    Icons.Filled.Notifications,
                    stringResource(R.string.profile_notifications),
                    showChevron = false,
                    trailing = { Switch(checked = notifications, onCheckedChange = onToggleNotifications) },
                )
                SettingsDivider()
                SettingsRow(
                    Icons.Filled.DarkMode,
                    stringResource(R.string.profile_dark_mode),
                    showChevron = false,
                    trailing = { Switch(checked = isDark, onCheckedChange = onToggleDark) },
                )
            }
            Spacer(Modifier.height(Spacing.lg))

            SectionKicker(stringResource(R.string.profile_other))
            SettingsGroup {
                SettingsRow(Icons.Filled.ChatBubble, stringResource(R.string.profile_feedback), onClick = onFeedback)
                SettingsDivider()
                SettingsRow(Icons.AutoMirrored.Filled.Help, stringResource(R.string.profile_help), onClick = onHelp)
                SettingsDivider()
                SettingsRow(Icons.Filled.Shield, stringResource(R.string.profile_privacy), onClick = onPrivacy)
                SettingsDivider()
                SettingsRow(
                    Icons.AutoMirrored.Filled.Logout,
                    stringResource(R.string.action_sign_out),
                    tint = MaterialTheme.colorScheme.error,
                    titleColor = MaterialTheme.colorScheme.error,
                    showChevron = false,
                    onClick = onSignOut,
                )
            }
            uiState.error?.let { message ->
                Spacer(Modifier.height(Spacing.md))
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(Spacing.xxl))
            Text(
                text = stringResource(R.string.profile_version),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extended.textTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.profile_copyright),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.extended.textTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun ProfileHeader(profile: TrainerProfile, email: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            InitialsAvatar(name = profile.fullName, size = 88.dp)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.extended.textSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(Spacing.md))
        Text(profile.fullName, style = MaterialTheme.typography.headlineSmall)
        email?.let {
            Spacer(Modifier.height(Spacing.xs))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.extended.textSecondary)
        }
        Spacer(Modifier.height(Spacing.sm))
        val roleText = if (profile.role == TrainerRole.OWNER) {
            stringResource(R.string.role_owner_salon, profile.salonName.orEmpty())
        } else {
            profile.role.label()
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f))
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        ) {
            Text(
                text = roleText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun SectionKicker(text: String) {
    Text(
        text = text,
        style = KickerLabel,
        color = MaterialTheme.extended.textTertiary,
        modifier = Modifier.padding(start = Spacing.sm, bottom = Spacing.sm),
    )
}
