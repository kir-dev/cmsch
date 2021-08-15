package hu.bme.sch.g7.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.model.GroupEntity
import hu.bme.sch.g7.model.MajorType

data class GroupEntityDto(
        @JsonIgnore
        val id: Int,

        @JsonView(FullDetails::class)
        val name: String,

        @JsonView(FullDetails::class)
        val major: MajorType,

        @JsonView(FullDetails::class)
        val staffs: List<GroupStaffDto>,

        @JsonView(FullDetails::class)
        val coverImageUrl: String,

        @JsonView(FullDetails::class)
        val lastLongitude: String,

        @JsonView(FullDetails::class)
        val lastLatitude: String,

        @JsonView(FullDetails::class)
        val lastTimeLocationChanged: Long,

        @JsonView(FullDetails::class)
        val lastTimeUpdatedUser: String
) {

    constructor(entity: GroupEntity) : this(
        entity.id,
        entity.name,
        entity.major,
            sequenceOf(entity.staff1, entity.staff2, entity.staff3, entity.staff4)
                    .filter { !it.isBlank() }
                    .map { it.split('|') }
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
