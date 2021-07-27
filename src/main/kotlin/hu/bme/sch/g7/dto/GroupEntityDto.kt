package hu.bme.sch.g7.dto

import hu.bme.sch.g7.model.GroupEntity
import hu.bme.sch.g7.model.MajorType

data class GroupEntityDto(
        val id: Int,
        val name: String,
        val major: MajorType,
        val staffs: List<GroupStaffDto>,
        val coverImageUrl: String,
        val lastLongitude: String,
        val lastLatitude: String,
        val lastTimeLocationChanged: Long,
        val lastTimeUpdatedUser: String
) {

    constructor(entity: GroupEntity) : this(
        entity.id,
        entity.name,
        entity.major,
            sequenceOf(entity.staff1, entity.staff2, entity.staff3, entity.staff4)
                    .filter { !it.isBlank() }
                    .map { it.split(';') }
                    .map {
                        GroupStaffDto(
                                it[0].trim(),
                                if (it.size > 1) it[1].trim() else "",
                                if (it.size > 2) it[2].trim() else "")
                    }
                    .toList(),
        entity.coverImageUrl,
        entity.lastLongitude,
        entity.lastLatitude,
        entity.lastTimeLocationChanged,
        entity.lastTimeUpdatedUser,
    )

}
