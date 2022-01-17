import { Box, Badge } from '@chakra-ui/react'
import React from 'react'

type StrDict = {
  [key: string]: string
}

const STATUS_TEXT_MAP: StrDict = {
  ACCEPTED: 'ELFOGADVA',
  NOT_SUBMITTED: 'BEADÁSRA VÁR',
  REJECTED: 'ELUTASÍTVA',
  SUBMITTED: 'ÉRTÉKELÉSRE VÁR',
  NOT_LOGGED_IN: 'NEM VAGY BEJELENTKEZVE'
}

const STATUS_COLOR_MAP: StrDict = {
  ACCEPTED: 'brand',
  NOT_SUBMITTED: 'gray',
  REJECTED: 'red',
  SUBMITTED: 'yellow',
  NOT_LOGGED_IN: 'gray'
}

type AchievementStatusBadgeProps = {
  status: string
  fontSize: string
}

export const AchievementStatusBadge: React.FC<AchievementStatusBadgeProps> = ({ status, fontSize }) => {
  return (
    <Box>
      <Badge colorScheme={STATUS_COLOR_MAP[status]} fontSize={fontSize}>
        {STATUS_TEXT_MAP[status]}
      </Badge>
    </Box>
  )
}
