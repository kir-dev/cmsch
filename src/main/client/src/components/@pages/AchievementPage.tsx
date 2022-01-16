import { Badge, Box, Flex, Heading, Skeleton, Spacer, Stack, Text } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { useParams } from 'react-router-dom'

import { mockData, statusColorMap, statusTextMap } from './AchievementList'

type AchievementPageProps = {}

export const AchievementPage: React.FC<AchievementPageProps> = (props) => {
  const { id }  = useParams()
  if (!id) {
    return null
  }
  const data = mockData.achievements.filter(ach => ach.id === parseInt(id))[0]

  return (
    <Page {...props}>
      <Flex align='center'>
        <Heading>{data.name}</Heading>
        <Spacer />
        <Box>
          <Badge colorScheme={statusColorMap.get(data.status)}><Text fontSize='lg'>{statusTextMap.get(data.status)}</Text></Badge>
        </Box>
      </Flex>

      <Stack>
        <Skeleton height='20px' />
        <Skeleton height='20px' />
        <Skeleton height='20px' />
      </Stack>
      
    </Page>
  )
}
