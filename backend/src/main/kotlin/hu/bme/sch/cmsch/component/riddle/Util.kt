package hu.bme.sch.cmsch.component.riddle

import kotlin.jvm.optionals.getOrNull

fun RiddleMappingEntity.toVirtualEntity(repository: RiddleEntityRepository): RiddleMappingVirtualEntity =
    repository.findById(riddleId).getOrNull().let { riddle ->
        RiddleMappingVirtualEntity(
            id,
            riddle?.categoryId ?: 0,
            riddle?.title ?: "n/a",
            hintUsed,
            completed,
            skipped,
            attemptCount,
            completedAt
        )
    }