package amrabed.android.release.evaluation.presentation.checklist.v2

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.domain.model.PrayerGroup
import amrabed.android.release.evaluation.domain.model.Ritual
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRitualSheet(
    ritual: Ritual? = null,
    onDismiss: () -> Unit,
    onSave: (Ritual) -> Unit
) {
    var title by remember { mutableStateOf(ritual?.title ?: "") }
    var selectedGroup by remember { mutableStateOf(ritual?.prayerGroup ?: PrayerGroup.MORNING) }
    var activeDays by remember { mutableStateOf(ritual?.activeDays ?: 127) }
    val context = LocalContext.current

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                if (ritual == null) stringResource(R.string.add_ritual) else stringResource(R.string.edit_ritual),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.ritual_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.prayer_group), style = MaterialTheme.typography.labelLarge)
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                items(PrayerGroup.values()) { group ->
                    val groupLabel = when(group) {
                        PrayerGroup.PRE_FAJR -> stringResource(R.string.ritual_group_pre_fajr)
                        PrayerGroup.FAJR -> stringResource(R.string.ritual_group_fajr)
                        PrayerGroup.MORNING -> stringResource(R.string.ritual_group_morning)
                        PrayerGroup.DHUHR_ASR -> stringResource(R.string.ritual_group_dhuhr_asr)
                        PrayerGroup.MAGHRIB -> stringResource(R.string.ritual_group_maghrib)
                        PrayerGroup.ISHA_NIGHT -> stringResource(R.string.ritual_group_isha_night)
                    }
                    FilterChip(
                        selected = selectedGroup == group,
                        onClick = { selectedGroup = group },
                        label = { Text(groupLabel) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.active_days), style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val days = context.resources.getStringArray(R.array.days)
                days.forEachIndexed { index, day ->
                    val isActive = (activeDays shr index) and 1 == 1
                    FilterChip(
                        selected = isActive,
                        onClick = {
                            activeDays = if (isActive) {
                                activeDays and (1 shl index).inv()
                            } else {
                                activeDays or (1 shl index)
                            }
                        },
                        label = { Text(day.take(1)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val newRitual = Ritual(
                        id = ritual?.id ?: UUID.randomUUID().toString(),
                        title = title.ifBlank { null },
                        defaultIndex = ritual?.defaultIndex ?: -1,
                        prayerGroup = selectedGroup,
                        sortOrder = ritual?.sortOrder ?: 0,
                        activeDays = activeDays,
                        isHidden = ritual?.isHidden ?: false,
                        createdAt = ritual?.createdAt ?: System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(newRitual)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() || (ritual?.defaultIndex ?: -1) != -1
            ) {
                Text(stringResource(R.string.save_ritual))
            }
        }
    }
}
