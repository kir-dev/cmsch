import { taskSubmissionStatus } from '../../../util/views/task.view'

export const taskSubmissionResponseMap = new Map<taskSubmissionStatus, string>([
  [taskSubmissionStatus.OK, 'Elküldve!'],
  [taskSubmissionStatus.EMPTY_ANSWER, 'Üres megoldás!'],
  [taskSubmissionStatus.INVALID_IMAGE, 'Érvénytelen kép!'],
  [taskSubmissionStatus.INVALID_PDF, 'Érvénytelen PDF!'],
  [taskSubmissionStatus.ALREADY_SUBMITTED, 'Ezt ön már beküldte!'],
  [taskSubmissionStatus.ALREADY_APPROVED, 'Önnek ezt a feladatát már elfogadták!'],
  [taskSubmissionStatus.NO_ASSOCIATE_GROUP, 'Nincs önnek csoportja!'],
  [taskSubmissionStatus.INVALID_TASK_ID, 'Érvénytelen feladat!'],
  [taskSubmissionStatus.TOO_EARLY_OR_LATE, 'Túl korán vagy túl későn adta be!'],
  [taskSubmissionStatus.NO_PERMISSION, 'Nincs jogosultsága!'],
  [taskSubmissionStatus.INVALID_BACKEND_CONFIG, 'Belső hiba']
])
