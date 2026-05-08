package amrabed.android.release.evaluation.presentation.checklist.v2

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.domain.model.PrayerGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ChecklistSectionHeader(
    prayerGroup: PrayerGroup,
    prayerTime: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val label = when(prayerGroup) {
            PrayerGroup.PRE_FAJR -> stringResource(R.string.ritual_group_pre_fajr)
            PrayerGroup.FAJR -> stringResource(R.string.ritual_group_fajr)
            PrayerGroup.MORNING -> stringResource(R.string.ritual_group_morning)
            PrayerGroup.DHUHR_ASR -> stringResource(R.string.ritual_group_dhuhr_asr)
            PrayerGroup.MAGHRIB -> stringResource(R.string.ritual_group_maghrib)
            PrayerGroup.ISHA_NIGHT -> stringResource(R.string.ritual_group_isha_night)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (prayerTime != null) {
            Text(
                text = prayerTime,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
