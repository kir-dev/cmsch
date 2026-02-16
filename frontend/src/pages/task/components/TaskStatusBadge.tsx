import { Badge } from '@chakra-ui/react'
import type { FC } from 'react'
import { TaskStatus } from '../../../util/views/task.view'

const STATUS_TEXT_MAP = new Map<TaskStatus, string>([
  [TaskStatus.ACCEPTED, 'ELFOGADVA'],
  [TaskStatus.NOT_SUBMITTED, 'BEADÁSRA VÁR'],
  [TaskStatus.REJECTED, 'ELUTASÍTVA'],
  [TaskStatus.SUBMITTED, 'ÉRTÉKELÉSRE VÁR'],
  [TaskStatus.NOT_LOGGED_IN, 'ÖN NINCS BEJELENTKEZVE']
])

const STATUS_COLOR_MAP = new Map<TaskStatus, string>([
  [TaskStatus.ACCEPTED, 'green'],
  [TaskStatus.NOT_SUBMITTED, 'gray'],
  [TaskStatus.REJECTED, 'red'],
  [TaskStatus.SUBMITTED, '#DE970B'], //dark yellow
  [TaskStatus.NOT_LOGGED_IN, 'gray']
])

type TaskStatusBadgeProps = {
  status: TaskStatus
  fontSize: string
}

export const TaskStatusBadge: FC<TaskStatusBadgeProps> = ({ status, fontSize }) => (
  <Badge variant="solid" bg={STATUS_COLOR_MAP.get(status)} fontSize={fontSize}>
    {STATUS_TEXT_MAP.get(status)}
  </Badge>
)
