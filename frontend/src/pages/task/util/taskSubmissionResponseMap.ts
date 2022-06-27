import { taskSubmissionStatus } from '../../../util/views/task.view'

export const taskSubmissionResponseMap = new Map<taskSubmissionStatus, string>([
  [taskSubmissionStatus.OK, 'Elküldve!'],
  [taskSubmissionStatus.EMPTY_ANSWER, 'Üres megoldás!'],
  [taskSubmissionStatus.INVALID_IMAGE, 'Érvénytelen kép!'],
  [taskSubmissionStatus.INVALID_PDF, 'Érvénytelen PDF!'],
  [taskSubmissionStatus.ALREADY_SUBMITTED, 'Már bekülted!'],
  [taskSubmissionStatus.ALREADY_APPROVED, 'Ezt a feladatodat már elfogadták!'],
  [taskSubmissionStatus.NO_ASSOCIATE_GROUP, 'Nincs csoportod!'],
  [taskSubmissionStatus.INVALID_TASK_ID, 'Érvénytelen feladat!'],
  [taskSubmissionStatus.TOO_EARLY_OR_LATE, 'Túl korán vagy túl későn adtad be!'],
  [taskSubmissionStatus.NO_PERMISSION, 'Nincs jogosultságod!'],
  [taskSubmissionStatus.INVALID_BACKEND_CONFIG, 'Belső hiba']
])
