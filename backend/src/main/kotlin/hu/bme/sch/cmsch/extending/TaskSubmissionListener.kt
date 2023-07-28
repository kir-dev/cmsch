package hu.bme.sch.cmsch.extending

import hu.bme.sch.cmsch.component.task.SubmittedTaskEntity
import hu.bme.sch.cmsch.component.task.TaskEntity
import hu.bme.sch.cmsch.model.UserEntity

interface TaskSubmissionListener {

    fun onTaskSubmit(user: UserEntity, task: TaskEntity, submission: SubmittedTaskEntity)

    fun onTaskUpdate(user: UserEntity, task: TaskEntity, submission: SubmittedTaskEntity)

}