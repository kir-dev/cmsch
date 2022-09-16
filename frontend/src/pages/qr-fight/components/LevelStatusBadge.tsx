import { LevelStatus, QrLevelDto } from '../../../util/views/qrFight.view'
import { Badge } from '@chakra-ui/react'

interface LevelStatusBadgeProps {
  level: QrLevelDto
}

export function LevelStatusBadge({ level }: LevelStatusBadgeProps) {
  let label = 'Ismeretlen'
  let color = 'gray'
  switch (level.status) {
    case LevelStatus.OPEN:
      label = 'Elérhető'
      color = 'yellow'
      break
    case LevelStatus.COMPLETED:
      label = 'Teljesítve'
      color = 'green'
      break
    case LevelStatus.NOT_UNLOCKED:
      label = 'Zárt'
      color = 'red'
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
