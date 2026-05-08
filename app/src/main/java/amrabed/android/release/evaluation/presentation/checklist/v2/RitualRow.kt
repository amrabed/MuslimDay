package amrabed.android.release.evaluation.presentation.checklist.v2

import amrabed.android.release.evaluation.domain.model.RitualWithLog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import amrabed.android.release.evaluation.utilities.preferences.Preferences

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RitualRow(
    ritualWithLog: RitualWithLog,
    isReadOnly: Boolean,
    onToggle: (Boolean) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ritual = ritualWithLog.ritual
    val log = ritualWithLog.log
    val isComplete = log?.isComplete == true
    val context = LocalContext.current

    val titleAlpha by animateFloatAsState(targetValue = if (isComplete) 0.6f else 1.0f, label = "alpha")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = !isReadOnly,
                onClick = { onToggle(!isComplete) },
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isComplete,
            onCheckedChange = if (isReadOnly) null else { _ -> onToggle(!isComplete) },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = ritual.title ?: (ritual.defaultIndex.takeIf { it >= 0 }?.let { Preferences.getDefaultTaskTitles(context)[it] } ?: ""),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (isComplete) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.alpha(titleAlpha)
        )
    }
}
