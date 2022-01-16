import { Box, Badge, Text } from '@chakra-ui/react'
import React from 'react'

const statusTextMap = new Map<string, string>([
  ['ACCEPTED', 'ELFOGADVA'],
  ['NOT_SUBMITTED', 'BEADÁSRA VÁR'],
  ['REJECTED', 'ELUTASÍTVA'],
  ['SUBMITTED', 'ÉRTÉKELÉSRE VÁR'],
  ['NOT_LOGGED_IN', 'NEM VAGY BEJELENTKEZVE'],
])

const statusColorMap = new Map<string, string>([
  ['ACCEPTED', 'brand'],
  ['NOT_SUBMITTED', 'gray'],
  ['REJECTED', 'red'],
  ['SUBMITTED', 'yellow'],
  ['NOT_LOGGED_IN', 'gray'],
])

type AchievementStatusBadgeProps = {
  status: string,
  fontSize: string
}

export const AchievementStatusBadge: React.FC<AchievementStatusBadgeProps> = (props) => {
    return (
      <Box>
        <Badge colorScheme={statusColorMap.get(props.status)}><Text fontSize={props.fontSize}>{statusTextMap.get(props.status)}</Text></Badge>
      </Box>
    )
}
