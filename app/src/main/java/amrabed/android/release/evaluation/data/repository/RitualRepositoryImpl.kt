package amrabed.android.release.evaluation.data.repository

import amrabed.android.release.evaluation.data.tables.v2.*
import amrabed.android.release.evaluation.domain.model.*
import amrabed.android.release.evaluation.domain.repository.PrayerTimeRepository
import amrabed.android.release.evaluation.domain.repository.RitualRepository
import kotlinx.coroutines.flow.*
import org.joda.time.LocalDate
import java.util.*

class RitualRepositoryImpl(
    private val ritualDao: RitualDao,
    private val ritualLogDao: RitualLogDao,
    private val prayerTimeRepository: PrayerTimeRepository
) : RitualRepository {

    override fun observeDailyChecklist(date: String): Flow<DailyChecklist> {
        val jodaDate = LocalDate.parse(date)
        val dayOfWeek = jodaDate.dayOfWeek

        return combine(
            ritualDao.getAllVisibleRituals(),
            ritualLogDao.getLogsForDate(date)
        ) { rituals, logs ->
            val ritualsWithLogs = rituals
                .map { entity -> entity.toDomain() }
                .filter { ritual -> ritual.isActiveOn(dayOfWeek) }
                .map { ritual ->
                    RitualWithLog(ritual, logs.find { it.ritualId == ritual.id }?.toDomain())
                }

            val groups = ritualsWithLogs
                .groupBy { it.ritual.prayerGroup }
                .map { (group, rituals) ->
                    ChecklistGroup(
                        prayerGroup = group,
                        prayerTime = null, // Will be populated below
                        rituals = rituals
                    )
                }
                .sortedBy { it.prayerGroup.ordinal }

            DailyChecklist(date, groups)
        }.flatMapLatest { checklist ->
            if (checklist.groups.isEmpty()) return@flatMapLatest flowOf(checklist)

            val groupFlows = checklist.groups.map { group ->
                prayerTimeRepository.getPrayerTime(group.prayerGroup.name, date).map { time ->
                    group.copy(prayerTime = time)
                }
            }
            combine(groupFlows) { updatedGroups ->
                checklist.copy(groups = updatedGroups.toList())
            }
        }
    }

    override suspend fun toggleRitual(ritualId: String, date: String, isComplete: Boolean) {
        if (isComplete) {
            val log = RitualLogEntity(
                id = UUID.randomUUID().toString(),
                ritualId = ritualId,
                date = date,
                completedAt = System.currentTimeMillis(),
                isComplete = true
            )
            ritualLogDao.insertLog(log)
        } else {
            ritualLogDao.deleteLog(ritualId, date)
        }
    }

    override suspend fun addRitual(ritual: Ritual) {
        ritualDao.insertRitual(ritual.toEntity())
    }

    override suspend fun updateRitual(ritual: Ritual) {
        ritualDao.updateRitual(ritual.toEntity())
    }

    override suspend fun hideRitual(ritualId: String) {
        ritualDao.getAllRituals().first().find { it.id == ritualId }?.let {
            ritualDao.updateRitual(it.copy(isHidden = true))
        }
    }

    override suspend fun deleteRitual(ritualId: String) {
        ritualDao.getAllRituals().first().find { it.id == ritualId }?.let {
            if (it.defaultIndex < 0) {
                ritualDao.deleteRitual(it)
            } else {
                hideRitual(ritualId)
            }
        }
    }

    override suspend fun reorderRituals(ritualIds: List<String>, prayerGroup: PrayerGroup) {
        ritualIds.forEachIndexed { index, id ->
            ritualDao.getAllRituals().first().find { it.id == id }?.let {
                ritualDao.updateRitual(it.copy(sortOrder = index, prayerGroup = prayerGroup.name))
            }
        }
    }

    private fun RitualEntity.toDomain() = Ritual(
        id = id,
        title = title,
        defaultIndex = defaultIndex,
        prayerGroup = PrayerGroup.valueOf(prayerGroup),
        sortOrder = sortOrder,
        activeDays = activeDays,
        isHidden = isHidden,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Ritual.toEntity() = RitualEntity(
        id = id,
        title = title,
        defaultIndex = defaultIndex,
        prayerGroup = prayerGroup.name,
        sortOrder = sortOrder,
        activeDays = activeDays,
        isHidden = isHidden,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun RitualLogEntity.toDomain() = RitualLog(
        id = id,
        ritualId = ritualId,
        date = date,
        completedAt = completedAt,
        isComplete = isComplete,
        note = note
    )
}
