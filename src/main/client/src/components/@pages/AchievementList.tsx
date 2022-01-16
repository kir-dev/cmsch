import { Box, Heading, StackDivider, VStack, Text, Badge, Flex, Spacer } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { Link } from 'react-router-dom'

type AchievementListProps = {}

export const mockData = {
  achievements: [
    {
      id: 1,
      name: 'Lorem ipsumdsadfsd fmkdsfdls fdskl fdskofsd',
      status: 'ACCEPTED',
      description: 'blalblal',
      type: 'BOTH',
    },
    {
      id: 2,
      name: 'Lorem ipsum2',
      status: 'REJECTED',
      description: 'fdsfdsgsd',
      comment: 'Nice work!',
      type: 'TEXT',
    },
    {
      id: 3,
      name: 'Lorem ipsum3',
      status: 'SUBMITTED',
      description: 'fdsfdsgsd',
      type: 'IMAGE',
    },
    {
      id: 4,
      name: 'Lorem ipsum4',
      status: 'NOT_SUBMITTED',
      description: 'fdsfdsgsd',
      type: 'BOTH',
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
          <Box key={item.id}>
            <Link to={`/bucketlist/${item.id}`}>
            <Flex>
              <Text fontSize='lg'>{item.name}</Text>
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
