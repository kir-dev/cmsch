package hu.bme.sch.g7.dto.view

import hu.bme.sch.g7.model.GroupEntity
import hu.bme.sch.g7.model.UserEntity

data class ProfileView(
        val userPreview: UserEntityPreview, // FIXME: ezt mindig le kell küldeni?
        val user: UserEntity,
        val group: GroupEntity? = null
)