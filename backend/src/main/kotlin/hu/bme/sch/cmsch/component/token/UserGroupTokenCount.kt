package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_NUMBER
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_TEXT
import hu.bme.sch.cmsch.model.IdentifiableEntity

class UserGroupTokenCount(
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "Csoport ID", order = -1)
    override var id: Int = 0,
    @property:GenerateOverview(columnName = "Csoport név", order = 1)
    var groupName: String = "",
    @property:GenerateOverview(columnName = "Tokenek száma", order = 6, renderer = OVERVIEW_TYPE_NUMBER)
    var tokenCount: Long = 0,
    @property:GenerateOverview(columnName = "Tokenek összpontszáma", order = 5, renderer = OVERVIEW_TYPE_NUMBER)
    var tokenPoints: Long = 0,
    @property:GenerateOverview(columnName = "Csoporttagok száma", order = 3, renderer = OVERVIEW_TYPE_NUMBER)
    var memberCount: Int = 0,
) : IdentifiableEntity {

    @property:GenerateOverview(columnName = "Korregált töredék", order = 4, renderer = OVERVIEW_TYPE_TEXT)
    var correctedPoints: Float = if (memberCount != 0) (tokenPoints.toFloat() / memberCount.toFloat()) else 0.0f

    @property:GenerateOverview(columnName = "Korrigált pontok", order = 2, renderer = OVERVIEW_TYPE_NUMBER)
    var finalPoints: Int = 0

}