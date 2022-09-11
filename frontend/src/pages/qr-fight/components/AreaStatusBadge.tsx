import { QrArea } from '../../../util/views/qrFight.view'
import { Badge } from '@chakra-ui/react'

interface AreaStatusBadgeProps {
  area: QrArea
}

export function AreaStatusBadge({ area }: AreaStatusBadgeProps) {
  switch (area.status) {
    case 'current':
      return <Badge colorScheme="yellow">Jelenlegi</Badge>
    case 'completed':
      return <Badge colorScheme="green">Teljesítve</Badge>
    default:
      return <Badge colorScheme="gray">Nem elérhető</Badge>
  }
}
