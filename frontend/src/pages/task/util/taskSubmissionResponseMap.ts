import { TaskSubmissionStatus } from '../../../util/views/task.view'

export const taskSubmissionResponseMap = new Map<TaskSubmissionStatus, string>([
  [TaskSubmissionStatus.OK, 'Elküldve!'],
  [TaskSubmissionStatus.EMPTY_ANSWER, 'Üres megoldás!'],
  [TaskSubmissionStatus.INVALID_IMAGE, 'Érvénytelen kép!'],
  [TaskSubmissionStatus.INVALID_PDF, 'Érvénytelen PDF!'],
  [TaskSubmissionStatus.INVALID_ZIP, 'Érvénytelen ZIP!'],
  [TaskSubmissionStatus.ALREADY_SUBMITTED, 'Ezt ön már beküldte!'],
  [TaskSubmissionStatus.ALREADY_APPROVED, 'Önnek ezt a feladatát már elfogadták!'],
  [TaskSubmissionStatus.NO_ASSOCIATE_GROUP, 'Nincs önnek csoportja!'],
  [TaskSubmissionStatus.INVALID_TASK_ID, 'Érvénytelen feladat!'],
  [TaskSubmissionStatus.TOO_EARLY_OR_LATE, 'Túl korán vagy túl későn adta be!'],
  [TaskSubmissionStatus.NO_PERMISSION, 'Nincs jogosultsága!'],
  [TaskSubmissionStatus.INVALID_BACKEND_CONFIG, 'Belső hiba']
])
