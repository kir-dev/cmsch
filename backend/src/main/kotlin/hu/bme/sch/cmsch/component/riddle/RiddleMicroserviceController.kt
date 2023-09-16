package hu.bme.sch.cmsch.component.riddle


class RiddleMicroserviceController(
    private val riddleComponent: RiddleComponent,
    private val riddleCacheManager: RiddleCacheManager
) {

    fun reloadComponentConfig() {
        riddleComponent.updateFromDatabase()
    }

    fun reloadRiddleAndCategoryCache() {
        riddleCacheManager.resetCache(persistMapping = false, overrideMappings = false)
    }

    fun reloadAll() {
        riddleCacheManager.resetCache(persistMapping = false, overrideMappings = true)
    }

    fun saveAll() {
        riddleCacheManager.resetCache(persistMapping = true, overrideMappings = true)
    }

    fun forceUnlockEverything() {
        riddleCacheManager.forceUnlock()
    }

}