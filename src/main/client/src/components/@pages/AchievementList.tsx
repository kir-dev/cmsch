import { Box, Heading, StackDivider, VStack, Text, Badge, Flex, Spacer } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { Link } from 'react-router-dom'

type AchievementListProps = {}

export const mockData = {
  achievements: [
    {
      achievement: {
        id: 1,
        title: 'Lorem ipsumdsadfsd fmkdsfdls fdskl fdskofsd',
        description: 'blalblal',
        type: 'BOTH',
      },
      status: 'ACCEPTED',
      comment: 'Nice work!',
    },
    {
      achievement: {
        id: 2,
        title: 'Lorem ipsum2',
        description: 'fdsfdsgsd',
        type: 'TEXT',
      },
      status: 'REJECTED',
      comment: 'nice try tho'
    },
    {
      achievement: {
        id: 3,
        title: 'Lorem ipsum3',
        description: 'fdsfdsgsd',
        type: 'IMAGE',
      },
      status: 'SUBMITTED',
    },
    {
      achievement: {
        id: 4,
        title: 'Lorem ipsum4',
        description: 'fdsfdsgsd',
        type: 'BOTH',
      },
      status: 'NOT_SUBMITTED',
    },
  ]
}

export const statusTextMap = new Map<string, string>([
  ['ACCEPTED', 'ELFOGADVA'],
  ['NOT_SUBMITTED', 'BEADÁSRA VÁR'],
  ['REJECTED', 'ELUTASÍTVA'],
  ['SUBMITTED', 'ÉRTÉKELÉSRE VÁR'],
  ['NOT_LOGGED_IN', 'NEM VAGY BEJELENTKEZVE'],
])

export const statusColorMap = new Map<string, string>([
  ['ACCEPTED', 'green'],
  ['NOT_SUBMITTED', 'gray'],
  ['REJECTED', 'red'],
  ['SUBMITTED', 'yellow'],
  ['NOT_LOGGED_IN', 'gray'],
])

export const AchievementList: React.FC<AchievementListProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Bucketlist</Heading>
      <VStack divider={<StackDivider borderColor='gray.200' />} spacing={4} align='stretch'>
        {mockData.achievements.map((item) => (
          <Box key={item.achievement.id}>
            <Link to={`/bucketlist/${item.achievement.id}`}>
            <Flex>
              <Text fontSize='lg'>{item.achievement.title}</Text>
              <Spacer />
              <Box>
                <Badge colorScheme={statusColorMap.get(item.status)}>{statusTextMap.get(item.status)}</Badge>
              </Box>
            </Flex>
            </Link>
          </Box>
        ))}
      </VStack>
    </Page>
  )
}
