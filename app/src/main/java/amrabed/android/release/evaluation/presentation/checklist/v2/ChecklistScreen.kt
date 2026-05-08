package amrabed.android.release.evaluation.presentation.checklist.v2

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.domain.model.Ritual
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.joda.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChecklistScreen(
    viewModel: ChecklistViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var ritualToEdit by remember { mutableStateOf<Ritual?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Max 7 days back as per spec FR-07
    val pageCount = 8
    val pagerState = rememberPagerState(initialPage = 7, pageCount = { pageCount })

    LaunchedEffect(pagerState.currentPage) {
        val date = LocalDate.now().minusDays(7 - pagerState.currentPage).toString()
        viewModel.onEvent(ChecklistEvent.NavigateToDate(date))
    }

    if (showAddSheet) {
        AddEditRitualSheet(
            onDismiss = { showAddSheet = false },
            onSave = {
                viewModel.onEvent(ChecklistEvent.AddRitual(it))
                showAddSheet = false
            }
        )
    }

    if (ritualToEdit != null) {
        AddEditRitualSheet(
            ritual = ritualToEdit,
            onDismiss = { ritualToEdit = null },
            onSave = {
                viewModel.onEvent(ChecklistEvent.UpdateRitual(it))
                ritualToEdit = null
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(uiState.dateLabel)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_ritual))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            DayProgressBar(
                completedCount = uiState.completedCount,
                totalCount = uiState.totalCount
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                if (page == pagerState.currentPage) {
                    if (uiState.totalCount == 0 && !uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.checklist_empty_title))
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            uiState.groups.forEach { group ->
                                stickyHeader {
                                    ChecklistSectionHeader(
                                        prayerGroup = group.prayerGroup,
                                        prayerTime = group.prayerTime
                                    )
                                }
                                items(group.rituals, key = { it.ritual.id }) { ritualWithLog ->
                                    RitualRow(
                                        ritualWithLog = ritualWithLog,
                                        isReadOnly = uiState.isReadOnly,
                                        onToggle = { isComplete ->
                                            viewModel.onEvent(ChecklistEvent.ToggleRitual(ritualWithLog.ritual.id, isComplete))
                                        },
                                        onLongClick = {
                                            ritualToEdit = ritualWithLog.ritual
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        val allCompleteMsg = stringResource(R.string.checklist_all_complete)
        LaunchedEffect(uiState.completedCount, uiState.totalCount) {
            if (uiState.completedCount == uiState.totalCount && uiState.totalCount > 0) {
                snackbarHostState.showSnackbar(allCompleteMsg)
            }
        }
    }
}
