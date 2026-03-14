import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'
import { LevelStatus } from '@/util/views/qrFight.view'

interface LevelStatusBadgeProps {
  levelStatus: LevelStatus
}

export function LevelStatusBadge({ levelStatus }: LevelStatusBadgeProps) {
  let label = 'Ismeretlen'
  let variant: 'default' | 'secondary' | 'destructive' | 'outline' = 'secondary'
  let customClass = ''

  switch (levelStatus) {
    case LevelStatus.OPEN:
      label = 'Elérhető'
      customClass = 'bg-warning text-warning-foreground hover:bg-warning/90'
      break
    case LevelStatus.COMPLETED:
      label = 'Teljesítve'
      customClass = 'bg-success text-success-foreground hover:bg-success/90'
      break
    case LevelStatus.NOT_UNLOCKED:
      label = 'Zárt'
      variant = 'destructive'
      break
    case LevelStatus.NOT_LOGGED_IN:
      label = 'Kijelentkezve'
      break
    case LevelStatus.NOT_ENABLED:
      label = 'Nem elérhető'
      break
  }
  return (
    <Badge variant={variant} className={cn('w-fit', customClass)}>
      {label}
    </Badge>
  )
}
