package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails

data class RiddleView(

    @field:JsonView(FullDetails::class)
    var imageUrl: String = "",

    @field:JsonView(FullDetails::class)
    var title: String = "",

    @field:JsonView(FullDetails::class)
    var hint: String? = null,

    @field:JsonView(FullDetails::class)
    var solved: Boolean = false,

    @field:JsonView(FullDetails::class)
    var skipped: Boolean = false,

    @field:JsonView(FullDetails::class)
    var creator: String? = null,

    @field:JsonView(FullDetails::class)
    var firstSolver: String? = null,

    @field:JsonView(FullDetails::class)
    var description: String = "",

    @field:JsonView(FullDetails::class)
    var skipPermitted: Boolean,
)

data class RiddleViewWithSolution(

    @field:JsonView(FullDetails::class)
    var imageUrl: String = "",

    @field:JsonView(FullDetails::class)
    var title: String = "",

    @field:JsonView(FullDetails::class)
    var hint: String? = null,

    @field:JsonView(FullDetails::class)
    var solved: Boolean = false,

    @field:JsonView(FullDetails::class)
    var skipped: Boolean = false,

    @field:JsonView(FullDetails::class)
    var creator: String? = null,

    @field:JsonView(FullDetails::class)
    var firstSolver: String? = null,

    @field:JsonView(FullDetails::class)
    var solution: String = "",

    @field:JsonView(FullDetails::class)
    var description: String = "",
)
