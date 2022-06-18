import { Badge, Box } from '@chakra-ui/react'
import { FC } from 'react'
import { taskStatus } from '../../../util/views/task.view'

const STATUS_TEXT_MAP = new Map<taskStatus, string>([
  [taskStatus.ACCEPTED, 'ELFOGADVA'],
  [taskStatus.NOT_SUBMITTED, 'BEADÁSRA VÁR'],
  [taskStatus.REJECTED, 'ELUTASÍTVA'],
  [taskStatus.SUBMITTED, 'ÉRTÉKELÉSRE VÁR'],
  [taskStatus.NOT_LOGGED_IN, 'NEM VAGY BEJELENTKEZVE']
])

const STATUS_COLOR_MAP = new Map<taskStatus, string>([
  [taskStatus.ACCEPTED, 'brand'],
  [taskStatus.NOT_SUBMITTED, 'gray'],
  [taskStatus.REJECTED, 'red'],
  [taskStatus.SUBMITTED, 'yellow'],
  [taskStatus.NOT_LOGGED_IN, 'gray']
])

type TaskStatusBadgeProps = {
  status: taskStatus
  fontSize: string
}

export const TaskStatusBadge: FC<TaskStatusBadgeProps> = ({ status, fontSize }) => (
  <Box>
    <Badge variant="solid" colorScheme={STATUS_COLOR_MAP.get(status)} fontSize={fontSize}>
      {STATUS_TEXT_MAP.get(status)}
    </Badge>
  </Box>
)
