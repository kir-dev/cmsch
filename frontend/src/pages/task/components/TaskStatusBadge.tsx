import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'
import { TaskStatus } from '@/util/views/task.view'
import type { FC } from 'react'

const STATUS_TEXT_MAP = new Map<TaskStatus, string>([
  [TaskStatus.ACCEPTED, 'ELFOGADVA'],
  [TaskStatus.NOT_SUBMITTED, 'BEADÁSRA VÁR'],
  [TaskStatus.REJECTED, 'ELUTASÍTVA'],
  [TaskStatus.SUBMITTED, 'ÉRTÉKELÉSRE VÁR'],
  [TaskStatus.NOT_LOGGED_IN, 'ÖN NINCS BEJELENTKEZVE']
])

const STATUS_CLASS_MAP = new Map<TaskStatus, string>([
  [TaskStatus.ACCEPTED, 'bg-success text-success-foreground hover:bg-success/90'],
  [TaskStatus.NOT_SUBMITTED, 'bg-secondary text-secondary-foreground hover:bg-secondary/90'],
  [TaskStatus.REJECTED, 'bg-danger text-danger-foreground hover:bg-danger/90'],
  [TaskStatus.SUBMITTED, 'bg-warning text-warning-foreground hover:bg-warning/90'],
  [TaskStatus.NOT_LOGGED_IN, 'bg-secondary text-secondary-foreground hover:bg-secondary/90']
])

type TaskStatusBadgeProps = {
  status: TaskStatus
  className?: string
}

export const TaskStatusBadge: FC<TaskStatusBadgeProps> = ({ status, className }) => (
  <Badge className={cn('border-none', STATUS_CLASS_MAP.get(status), className)}>{STATUS_TEXT_MAP.get(status)}</Badge>
)
