package hu.bme.sch.g7.dto

import com.fasterxml.jackson.annotation.JsonView

data class TopListEntryDto(
        @JsonView(value = [ Preview::class, FullDetails::class ])
        val name: String,

        @JsonView(value = [ Preview::class, FullDetails::class ])
        val score: Int
)