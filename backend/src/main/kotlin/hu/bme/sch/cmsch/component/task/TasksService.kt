package hu.bme.sch.cmsch.component.task

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.uploadFile
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

const val HOUR = 60 * 60

@Service
@ConditionalOnBean(TaskComponent::class)
open class TasksService(
    private val taskRepository: TaskEntityRepository,
    private val submitted: SubmittedTaskRepository,
    private val categories: TaskCategoryRepository,
    private val clock: TimeService
) {

    companion object {
        private val target = "task"
    }

    @Transactional(readOnly = true)
    open fun getById(id: Int): Optional<TaskEntity> {
        return taskRepository.findById(id)
    }

    @Transactional(readOnly = true)
    open fun getSubmissionForUserOrNull(user: UserEntity, task: Optional<TaskEntity>): SubmittedTaskEntity? {
        if (task.isEmpty)
            return null

        return submitted.findByTask_IdAndUserId(task.get().id, user.id)
            .orElse(null)
    }

    @Transactional(readOnly = true)
    open fun getSubmissionForGroupOrNull(group: GroupEntity, task: Optional<TaskEntity>): SubmittedTaskEntity? {
        if (task.isEmpty)
            return null

        return submitted.findByTask_IdAndGroupId(task.get().id, group.id)
                .orElse(null)
    }

    @Transactional(readOnly = true)
    open fun getHighlightedOnes(group: GroupEntity): List<TaskEntityWrapperDto> {
        return taskRepository.findAllByHighlightedTrueAndVisibleTrue()
                .map { findSubmissionStatusForGroup(it, group) }
    }

    @Transactional(readOnly = true)
    open fun getAllTasksForGroup(group: GroupEntity): List<TaskEntityWrapperDto> {
        return taskRepository.findAllByVisibleTrue()
                .map { findSubmissionStatusForGroup(it, group) }
    }

    @Transactional(readOnly = true)
    open fun getAllTasksForUser(user: CmschUser): List<TaskEntityWrapperDto> {
        return taskRepository.findAllByVisibleTrue()
            .map { findSubmissionStatusForUser(it, user) }
    }

    @Transactional(readOnly = true)
    open fun getAllTasksForGuests(): List<TaskEntityWrapperDto> {
        return taskRepository.findAllByVisibleTrue()
                .map { TaskEntityWrapperDto(it, TaskStatus.NOT_LOGGED_IN, "") }
    }

    private fun findSubmissionStatusForGroup(taskEntity: TaskEntity, group: GroupEntity): TaskEntityWrapperDto {
        val submission = submitted.findByTask_IdAndGroupId(taskEntity.id, group.id)
        return findSubmission(submission, taskEntity)
    }

    private fun findSubmissionStatusForUser(taskEntity: TaskEntity, user: CmschUser): TaskEntityWrapperDto {
        val submission = submitted.findByTask_IdAndUserId(taskEntity.id, user.id)
        return findSubmission(submission, taskEntity)
    }

    private fun findSubmission(submission: Optional<SubmittedTaskEntity>, taskEntity: TaskEntity): TaskEntityWrapperDto {
        return when {
            submission.isEmpty -> TaskEntityWrapperDto(taskEntity, TaskStatus.NOT_SUBMITTED, "")
            submission.get().rejected -> TaskEntityWrapperDto(taskEntity, TaskStatus.REJECTED, submission.get().response)
            submission.get().approved -> TaskEntityWrapperDto(taskEntity, TaskStatus.ACCEPTED, submission.get().response)
            else -> TaskEntityWrapperDto(taskEntity, TaskStatus.SUBMITTED, submission.get().response)
        }
    }


    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitTaskForGroup(answer: TaskSubmissionDto, file: MultipartFile?, user: UserEntity): TaskSubmissionStatus {
        val groupId = user.group?.id ?: return TaskSubmissionStatus.NO_ASSOCIATE_GROUP
        val task = taskRepository.findById(answer.taskId).orElse(null)
                ?: return TaskSubmissionStatus.INVALID_TASK_ID

        if (task.availableFrom - (4 * HOUR) > clock.getTimeInSeconds()
                || task.availableTo + (4 * HOUR) < clock.getTimeInSeconds()) {
            return TaskSubmissionStatus.TOO_EARLY_OR_LATE
        }

        val previous = submitted.findByTask_IdAndGroupId(answer.taskId, groupId)
        if (previous.isPresent) {
            val submission = previous.get()
            if (submission.approved)
                return TaskSubmissionStatus.ALREADY_APPROVED
            if (!submission.approved && !submission.rejected)
                return TaskSubmissionStatus.ALREADY_SUBMITTED
            return updateSubmission(task, answer, file, submission)

        } else {
            return newSubmission(task, answer, groupId, null, user, file)
        }
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun submitTaskForUser(answer: TaskSubmissionDto, file: MultipartFile?, user: UserEntity): TaskSubmissionStatus {
        val task = taskRepository.findById(answer.taskId).orElse(null)
            ?: return TaskSubmissionStatus.INVALID_TASK_ID

        if (task.availableFrom - (4 * HOUR) > clock.getTimeInSeconds()
            || task.availableTo + (4 * HOUR) < clock.getTimeInSeconds()) {
            return TaskSubmissionStatus.TOO_EARLY_OR_LATE
        }

        val previous = submitted.findByTask_IdAndUserId(answer.taskId, user.id)
        if (previous.isPresent) {
            val submission = previous.get()
            if (submission.approved)
                return TaskSubmissionStatus.ALREADY_APPROVED
            if (!submission.approved && !submission.rejected)
                return TaskSubmissionStatus.ALREADY_SUBMITTED
            return updateSubmission(task, answer, file, submission)

        } else {
            return newSubmission(task, answer, null, user.id, user, file)
        }
    }

    private fun newSubmission(
        task: TaskEntity,
        answer: TaskSubmissionDto,
        groupId: Int?,
        userId: Int?,
        user: UserEntity,
        file: MultipartFile?
    ): TaskSubmissionStatus {

        val groupName = if (groupId != null) user.group?.name else null
        val userName = if (userId != null) user.fullName else null

        when (task.type) {
            TaskType.TEXT -> {
                if (answer.textAnswer.isBlank())
                    return TaskSubmissionStatus.EMPTY_ANSWER

                submitted.save(SubmittedTaskEntity(
                    0, task, groupId, groupName ?: "",
                    userId, userName ?: "",
                    task.categoryId, answer.textAnswer, "",
                    "", approved = false, rejected = false, score = 0
                ))
                return TaskSubmissionStatus.OK

            }
            TaskType.IMAGE -> {
                if (file == null || imageFileNameInvalid(file))
                    return TaskSubmissionStatus.INVALID_IMAGE

                val fileName = file.uploadFile(target)

                submitted.save(SubmittedTaskEntity(
                    0, task, groupId, groupName ?: "",
                    userId, userName ?: "",
                    task.categoryId, "", imageUrlAnswer = "$target/$fileName",
                    response = "", approved = false, rejected = false, score = 0
                ))
                return TaskSubmissionStatus.OK

            }
            TaskType.BOTH -> {
                val fileName = file?.uploadFile(target) ?: ""

                submitted.save(SubmittedTaskEntity(
                    0, task, groupId, groupName ?: "",
                    userId, userName ?: "",
                    task.categoryId, answer.textAnswer, imageUrlAnswer = "$target/$fileName",
                    response = "", approved = false, rejected = false, score = 0
                ))
                return TaskSubmissionStatus.OK

            }
            TaskType.ONLY_PDF -> {
                if (file == null || pdfFileNameInvalid(file))
                    return TaskSubmissionStatus.INVALID_PDF

                val fileName = file.uploadFile(target)

                submitted.save(SubmittedTaskEntity(
                    0, task, groupId, groupName ?: "",
                    userId, userName ?: "",
                    task.categoryId, "", fileUrlAnswer = "$target/$fileName",
                    response = "", approved = false, rejected = false, score = 0
                ))
                return TaskSubmissionStatus.OK
            }
            else -> {
                throw IllegalStateException("Invalid task type: " + task.type)
            }
        }
    }

    private fun updateSubmission(
        task: TaskEntity,
        answer: TaskSubmissionDto,
        file: MultipartFile?,
        submission: SubmittedTaskEntity
    ): TaskSubmissionStatus {

        when (task.type) {
            TaskType.TEXT -> {
                if (answer.textAnswer.isBlank())
                    return TaskSubmissionStatus.EMPTY_ANSWER

                submission.textAnswer = answer.textAnswer
                submission.rejected = false
                submitted.save(submission)
                return TaskSubmissionStatus.OK

            }
            TaskType.IMAGE -> {
                if (file == null || imageFileNameInvalid(file))
                    return TaskSubmissionStatus.INVALID_IMAGE
                val fileName = file.uploadFile(target)

                submission.imageUrlAnswer = "$target/$fileName"
                submission.rejected = false
                submitted.save(submission)
                return TaskSubmissionStatus.OK

            }
            TaskType.BOTH -> {
                if (file != null && !imageFileNameInvalid(file)) {
                    val fileName = file.uploadFile(target)
                    submission.imageUrlAnswer = "$target/$fileName"
                }
                submission.textAnswer = answer.textAnswer
                submission.rejected = false
                submitted.save(submission)
                return TaskSubmissionStatus.OK

            }
            TaskType.ONLY_PDF -> {
                if (file == null || pdfFileNameInvalid(file))
                    return TaskSubmissionStatus.INVALID_PDF
                val fileName = file.uploadFile(target)

                submission.fileUrlAnswer = "$target/$fileName"
                submission.rejected = false
                submitted.save(submission)
                return TaskSubmissionStatus.OK

            }
            else -> {
                throw IllegalStateException("Invalid task type: " + task.type)
            }
        }
    }

    private fun imageFileNameInvalid(file: MultipartFile): Boolean {
        return file.originalFilename == null || (
                !file.originalFilename!!.lowercase().endsWith(".png")
                        && !file.originalFilename!!.lowercase().endsWith(".jpg")
                        && !file.originalFilename!!.lowercase().endsWith(".jpeg")
                        && !file.originalFilename!!.lowercase().endsWith(".gif")
                )
    }

    private fun pdfFileNameInvalid(file: MultipartFile): Boolean {
        return file.originalFilename == null || (!file.originalFilename!!.lowercase().endsWith(".pdf"))
    }

    @Transactional(readOnly = true)
    open fun getCategoriesForGroup(groupId: Int): List<TaskCategoryDto> {
        val submissionByCategory = submitted.findAllByGroupId(groupId)
                .groupBy { it.categoryId }

        val taskByCategory = taskRepository.findAllByVisibleTrue()

        return categories.findAll()
                .map { category ->
                    return@map TaskCategoryDto(
                            name = category.name,
                            categoryId = category.categoryId,
                            availableFrom = category.availableFrom,
                            availableTo = category.availableTo,
                            type = category.type,

                            sum = taskByCategory.count { it.categoryId == category.categoryId },
                            approved = submissionByCategory[category.categoryId]?.count { it.approved } ?: 0,
                            rejected = submissionByCategory[category.categoryId]?.count { it.rejected } ?: 0,
                            notGraded = submissionByCategory[category.categoryId]?.count { !it.approved && !it.rejected } ?: 0
                    )
                }
    }

    @Transactional(readOnly = true)
    open fun getCategoriesForUser(userId: Int): List<TaskCategoryDto> {
        val submissionByCategory = submitted.findAllByUserId(userId)
            .groupBy { it.categoryId }

        val taskByCategory = taskRepository.findAllByVisibleTrue()

        return categories.findAll()
            .map { category ->
                return@map TaskCategoryDto(
                    name = category.name,
                    categoryId = category.categoryId,
                    availableFrom = category.availableFrom,
                    availableTo = category.availableTo,
                    type = category.type,

                    sum = taskByCategory.count { it.categoryId == category.categoryId },
                    approved = submissionByCategory[category.categoryId]?.count { it.approved } ?: 0,
                    rejected = submissionByCategory[category.categoryId]?.count { it.rejected } ?: 0,
                    notGraded = submissionByCategory[category.categoryId]?.count { !it.approved && !it.rejected } ?: 0
                )
            }
    }

    @Transactional(readOnly = true)
    open fun getCategoryName(categoryId: Int): String {
        return categories.findAllByCategoryId(categoryId).map { it.name }.firstOrNull() ?: "Nincs ilyen"
    }

    @Transactional(readOnly = true)
    open fun getCategoryAvailableFrom(categoryId: Int): Long {
        return categories.findAllByCategoryId(categoryId).map { it.availableFrom }.firstOrNull() ?: 0
    }

    @Transactional(readOnly = true)
    open fun getCategory(categoryId: Int): TaskCategoryEntity? {
        return categories.findAllByCategoryId(categoryId).firstOrNull()
    }

    @Transactional(readOnly = true)
    open fun getAllSubmissions(it: GroupEntity): List<SubmittedTaskEntity> {
        return submitted.findAllByGroupId(it.id)
    }

    @Transactional(readOnly = true)
    open fun getAllCategories(): List<TaskCategoryEntity> {
        return categories.findAll()
    }

    @Transactional(readOnly = true)
    open fun getTotalTasksForUser(user: UserEntity): Int {
        return taskRepository.findAllByVisibleTrue().size
    }

    @Transactional(readOnly = true)
    open fun getSubmittedTasksForUser(user: UserEntity): Int {
        return submitted.findAllByUserIdAndRejectedFalseAndApprovedFalse(user.id).size
    }

    @Transactional(readOnly = true)
    open fun getCompletedTasksForUser(user: UserEntity): Int {
        return submitted.findAllByUserIdAndRejectedFalseAndApprovedTrue(user.id).size
    }

    @Transactional(readOnly = true)
    open fun getTasksThatNeedsToBeCompleted(user: UserEntity): List<String> {
        return categories.findAllByType(TaskCategoryType.PROFILE_REQUIRED)
            .flatMap { taskRepository.findAllByCategoryIdAndVisibleTrue(it.categoryId) }
            .filter { task -> submitted.findByTask_IdAndUserId(task.id, user.id)
                .map { !it.approved }
                .orElse(true)
            }
            .map { it.title }
    }

}
