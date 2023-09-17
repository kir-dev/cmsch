package hu.bme.sch.cmsch.extending

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.component.task.SubmittedTaskEntity
import hu.bme.sch.cmsch.component.task.TaskEntity

interface TaskSubmissionListener {

    fun onTaskSubmit(user: CmschUser, task: TaskEntity, submission: SubmittedTaskEntity)

    fun onTaskUpdate(user: CmschUser, task: TaskEntity, submission: SubmittedTaskEntity)

}