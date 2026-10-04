package com.framex.app.ui.screens.about.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.framex.app.R

private val DialogBackground = Color(0xFF111218)

enum class LegalDialogType(
    val title: String,
    val subtitle: String,
    val webUrl: String,
    val iconRes: Int
) {
    OPEN_SOURCE_LICENSES(
        title = "Open Source Licenses",
        subtitle = "Third-party libraries and licenses",
        webUrl = "https://github.com/MaheshSharan/FrameX-Android/blob/main/LICENSE",
        iconRes = R.drawable.ic_doc_text
    ),
    PRIVACY_POLICY(
        title = "Privacy Policy",
        subtitle = "How we handle your data",
        webUrl = "https://maheshsharan.github.io/FrameX-Android/privacy-policy",
        iconRes = R.drawable.ic_shield_check
    ),
    TERMS_OF_SERVICE(
        title = "Terms of Service",
        subtitle = "Usage terms and conditions",
        webUrl = "https://github.com/MaheshSharan/FrameX-Android/blob/main/KNOWN_LIMITATIONS.md",
        iconRes = R.drawable.ic_doc_text
    )
}

/**
 * In-app legal viewer dialog providing verified content for open source licenses,
 * privacy policy, and terms of service with direct external web verification.
 */
@Composable
fun LegalInfoDialog(
    type: LegalDialogType,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DialogBackground),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                LegalDialogHeader(type = type)

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (type) {
                        LegalDialogType.OPEN_SOURCE_LICENSES -> LicensesDialogContent()
                        LegalDialogType.PRIVACY_POLICY -> PrivacyPolicyDialogContent()
                        LegalDialogType.TERMS_OF_SERVICE -> TermsOfServiceDialogContent()
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LegalDialogActions(
                    type = type,
                    onDismiss = onDismiss,
                    onOpenUrl = onOpenUrl
                )
            }
        }
    }
}

@Composable
private fun LegalDialogHeader(type: LegalDialogType) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = type.iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = type.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = type.subtitle,
                color = Color(0xFFA0A0A0),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun LicensesDialogContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "FrameX is built on open source technologies. The primary third-party libraries and frameworks include:",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.5.sp,
            lineHeight = 17.sp
        )

        LicenseLibraryItem("Jetpack Compose & Material 3", "Apache License 2.0", "Google LLC")
        LicenseLibraryItem("Kotlin & Coroutines", "Apache License 2.0", "JetBrains s.r.o.")
        LicenseLibraryItem("Hilt & Dagger Dependency Injection", "Apache License 2.0", "Google LLC")
        LicenseLibraryItem("Shizuku System Framework", "Apache License 2.0", "RikkaApps")
        LicenseLibraryItem("AndroidX Core & DataStore", "Apache License 2.0", "The Android Open Source Project")
    }
}

@Composable
private fun LicenseLibraryItem(name: String, license: String, copyright: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF171822))
            .padding(10.dp)
    ) {
        Column {
            Text(text = name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "$license • $copyright", color = Color(0xFFA0A0A0), fontSize = 11.5.sp)
        }
    }
}

@Composable
private fun PrivacyPolicyDialogContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PolicyPoint(
            title = "Zero Data Collection",
            body = "FrameX does not collect, record, transmit, or monetize any personal information, location data, or device analytics."
        )
        PolicyPoint(
            title = "100% Local Device Storage",
            body = "Your custom overlay positions, appearance preferences, and game whitelists are stored strictly on-device in DataStore."
        )
        PolicyPoint(
            title = "Hardware & Privileged Telemetry",
            body = "System permissions and Shizuku Binder access are used exclusively to query real-time FPS, CPU/GPU frequencies, and thermal metrics during active gameplay."
        )
    }
}

@Composable
private fun PolicyPoint(title: String, body: String) {
    Column {
        Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = body, color = Color(0xFFA0A0A0), fontSize = 12.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun TermsOfServiceDialogContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PolicyPoint(
            title = "As-Is Open-Source Toolkit",
            body = "FrameX is provided \"AS IS\" under the Apache License 2.0. Users have full control over hardware optimization toggles."
        )
        PolicyPoint(
            title = "Privileged Access Disclaimers",
            body = "Advanced tuning features operate via privileged ADB/Shizuku APIs. FrameX includes atomic baseline recovery routines to revert all modified system settings upon session end."
        )
        PolicyPoint(
            title = "OEM Compatibility",
            body = "Vendor overrides are selectively guarded by brand diagnostics to prevent unverified overrides on sensitive hardware configurations."
        )
    }
}

@Composable
private fun LegalDialogActions(
    type: LegalDialogType,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f))
        ) {
            Text(text = "Close", color = Color.White, fontSize = 13.sp)
        }

        Button(
            onClick = {
                onOpenUrl(type.webUrl)
                onDismiss()
            },
            modifier = Modifier.weight(1.2f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "View Web", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
