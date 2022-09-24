import { Badge, Box } from '@chakra-ui/react'
import { FC } from 'react'
import { taskStatus } from '../../../util/views/task.view'

const STATUS_TEXT_MAP = new Map<taskStatus, string>([
  [taskStatus.ACCEPTED, 'ELFOGADVA'],
  [taskStatus.NOT_SUBMITTED, 'BEADÁSRA VÁR'],
  [taskStatus.REJECTED, 'ELUTASÍTVA'],
  [taskStatus.SUBMITTED, 'ÉRTÉKELÉSRE VÁR'],
  [taskStatus.NOT_LOGGED_IN, 'ÖN NINCS BEJELENTKEZVE']
])

const STATUS_COLOR_MAP = new Map<taskStatus, string>([
  [taskStatus.ACCEPTED, 'green'],
  [taskStatus.NOT_SUBMITTED, 'gray'],
  [taskStatus.REJECTED, 'red'],
  [taskStatus.SUBMITTED, '#DE970B'], //dark yellow
  [taskStatus.NOT_LOGGED_IN, 'gray']
])

type TaskStatusBadgeProps = {
  status: taskStatus
  fontSize: string
}

export const TaskStatusBadge: FC<TaskStatusBadgeProps> = ({ status, fontSize }) => (
  <Box>
    <Badge variant="solid" bg={STATUS_COLOR_MAP.get(status)} fontSize={fontSize}>
      {STATUS_TEXT_MAP.get(status)}
    </Badge>
  </Box>
)
