import { LevelStatus, QrLevelDto } from '../../../util/views/qrFight.view'
import { Badge } from '@chakra-ui/react'

interface LevelStatusBadgeProps {
  level: QrLevelDto
}

export function LevelStatusBadge({ level }: LevelStatusBadgeProps) {
  let label = 'Ismeretlen'
  let color = 'border'
  switch (level.status) {
    case LevelStatus.OPEN:
      label = 'Elérhető'
      color = 'warning'
      break
    case LevelStatus.COMPLETED:
      label = 'Teljesítve'
      color = 'success'
      break
    case LevelStatus.NOT_UNLOCKED:
      label = 'Zárt'
      color = 'error'
      break
    case LevelStatus.NOT_LOGGED_IN:
      label = 'Kijelentkezve'
      break
    case LevelStatus.NOT_ENABLED:
      label = 'Nem elérhető'
      break
  }
  return (
    <Badge w="fit-content" colorScheme={color}>
      {label}
    </Badge>
  )
}
