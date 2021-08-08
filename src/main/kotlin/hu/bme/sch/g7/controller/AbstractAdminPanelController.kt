package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.INPUT_TYPE_FILE
import hu.bme.sch.g7.admin.INTERPRETER_INHERIT
import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.model.ManagedEntity
import hu.bme.sch.g7.model.UserEntity
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.uploadFile
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.function.Supplier
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

const val INVALID_ID_ERROR = "INVALID_ID"
const val CONTROL_MODE_EDIT_DELETE = "edit,delete"
const val CONTROL_MODE_EDIT = "edit"
const val CONTROL_MODE_VIEW = "view"

open class AbstractAdminPanelController<T : ManagedEntity>(
        val repo: CrudRepository<T, Int>,
        val view: String,
        val titleSingular: String,
        val titlePlural: String,
        val description: String,
        classType: KClass<T>,
        val supplier: Supplier<T>,
        val entitySourceMapping: Map<String, (T?) -> List<String>> =
                mapOf(Nothing::class.simpleName!! to { listOf() }),
        val controlMode: String = CONTROL_MODE_EDIT_DELETE
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val descriptor = OverviewBuilder(classType)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", descriptor.getColumns())
        model.addAttribute("fields", descriptor.getColumnDefinitions())
        model.addAttribute("rows", repo.findAll())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", controlMode)

        return "overview"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", controlMode)

        val entity = repo.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("data", entity.orElseThrow())
        }
        return "details"
    }

    @GetMapping("/create")
    fun create(model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", false)
        model.addAttribute("view", view)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("data", null)
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", controlMode)

        return "details"
    }

    @GetMapping("/delete/{id}")
    fun deleteConfirm(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("user", request.getUser())

        val entity = repo.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("item", entity.orElseThrow().toString())
        }
        return "delete"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Int): String {
        val entity = repo.findById(id).orElseThrow()
        repo.delete(entity)
        onEntityChanged(entity)
        return "redirect:/admin/control/$view/"
    }

    @PostMapping("/create")
    fun create(@ModelAttribute(binding = false) dto: T,
               @RequestParam(required = false) file0: MultipartFile?,
               @RequestParam(required = false) file1: MultipartFile?,
               request: HttpServletRequest
    ): String {
        val entity = supplier.get()
        updateEntity(descriptor, request.getUser(), entity, dto, file0, file1)
        entity.id = 0
        onEntityPreSave(entity, request)
        repo.save(entity)
        onEntityChanged(entity)
        return "redirect:/admin/control/$view/"
    }

    @PostMapping("/edit/{id}")
    fun edit(@PathVariable id: Int,
             @ModelAttribute(binding = false) dto: T,
             @RequestParam(required = false) file0: MultipartFile?,
             @RequestParam(required = false) file1: MultipartFile?,
             request: HttpServletRequest
    ): String {
        val entity = repo.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/edit/$id"
        }

        updateEntity(descriptor, request.getUser(), entity.get(), dto, file0, file1)
        entity.get().id = id
        onEntityPreSave(entity.get(), request)
        repo.save(entity.get())
        onEntityChanged(entity.get())
        return "redirect:/admin/control/$view"
    }

    private fun updateEntity(descriptor: OverviewBuilder, user: UserEntity, entity: T, dto: T, file0: MultipartFile?, file1: MultipartFile?) {
        descriptor.getInputs().forEach {
            if (it.first is KMutableProperty1<out Any, *> && !it.second.ignore && it.second.minimumRole.value <= user.role.value) {
                when {
                    it.second.interpreter == INTERPRETER_INHERIT && it.second.type == INPUT_TYPE_FILE -> {
                        when {
                            it.second.fileId == "0" -> {
                                file0?.uploadFile(view).let { file ->
                                    (it.first as KMutableProperty1<out Any, *>).setter.call(entity, "$view/$file")
                                }
                            }
                            it.second.fileId == "1" -> {
                                file1?.uploadFile(view).let { file ->
                                    (it.first as KMutableProperty1<out Any, *>).setter.call(entity, "cdn/$view/$file")
                                }
                            }
                            else -> {
                                log.error("Invalid file field name: file${it.second.fileId}")
                            }
                        }
                    }
                    it.second.interpreter == INTERPRETER_INHERIT && it.second.type != INPUT_TYPE_FILE -> {
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, it.first.getter.call(dto))
                    }
                    it.second.interpreter == "path" -> {
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, it.first.getter.call(dto)
                                .toString()
                                .lowercase()
                                .replace(" ", "-")
                                .replace(Regex("[^a-z0-9-.]"), ""))
                    }

                }
            }
        }
    }

    open fun onEntityChanged(entity: T) {
        // Overridden when notification is required
    }

    open fun onEntityPreSave(entity: T, request: HttpServletRequest) {
        // Overridden when notification is required
    }

}