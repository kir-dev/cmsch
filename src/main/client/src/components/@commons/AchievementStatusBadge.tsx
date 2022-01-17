import { Box, Badge } from '@chakra-ui/react'
import React from 'react'

import { achievementStatus } from '../../types/dto/achievements'

const STATUS_TEXT_MAP = new Map<achievementStatus, string>([
  [achievementStatus.ACCEPTED, 'ELFOGADVA'],
  [achievementStatus.NOT_SUBMITTED, 'BEADÁSRA VÁR'],
  [achievementStatus.REJECTED, 'ELUTASÍTVA'],
  [achievementStatus.SUBMITTED, 'ÉRTÉKELÉSRE VÁR'],
  [achievementStatus.NOT_LOGGED_IN, 'NEM VAGY BEJELENTKEZVE']
])

const STATUS_COLOR_MAP = new Map<achievementStatus, string>([
  [achievementStatus.ACCEPTED, 'brand'],
  [achievementStatus.NOT_SUBMITTED, 'gray'],
  [achievementStatus.REJECTED, 'red'],
  [achievementStatus.SUBMITTED, 'yellow'],
  [achievementStatus.NOT_LOGGED_IN, 'gray']
])

type AchievementStatusBadgeProps = {
  status: achievementStatus
  fontSize: string
}

export const AchievementStatusBadge: React.FC<AchievementStatusBadgeProps> = ({ status, fontSize }) => {
  return (
    <Box>
      <Badge colorScheme={STATUS_COLOR_MAP.get(status)} fontSize={fontSize}>
        {STATUS_TEXT_MAP.get(status)}
      </Badge>
    </Box>
  )
}
