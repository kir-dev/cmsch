package hu.bme.sch.cmsch.component.event

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.PERMISSION_CONTROL_EVENTS
import hu.bme.sch.cmsch.service.PermissionValidator
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/event")
@ConditionalOnBean(EventComponent::class)
class EventAdminController(
    adminMenuService: AdminMenuService,
    component: EventComponent,
) : ComponentApiBase(
    adminMenuService,
    EventComponent::class.java,
    component,
    PERMISSION_CONTROL_EVENTS,
    "Események",
    "Események testreszabása"
)

@Controller
@RequestMapping("/admin/control/events")
@ConditionalOnBean(EventComponent::class)
class EventsController(
    repo: EventRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: EventComponent
) : AbstractAdminPanelController<EventEntity>(
    repo,
    "events", "Esemény", "Események",
    "A rendezvény összes (publikus) programjainak kezelse.",
    EventEntity::class, ::EventEntity, importService, adminMenuService, component,
    permissionControl = PermissionValidator() { it.isAdmin() ?: false || it.grantMedia ?: false },
    importable = true, adminMenuIcon = "event"
)
