import { Box, Heading, StackDivider, VStack, Text, Flex, Spacer } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { Link } from 'react-router-dom'

import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'

type AchievementListProps = {}

export const mockData = {
  achievements: [
    {
      achievement: {
        id: 1,
        title: `Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
          sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.`,
        description: 'blalblal',
        type: 'BOTH'
      },
      status: 'ACCEPTED',
      comment: 'Nice work!',
      submission: {
        approved: true,
        imageUrlAnswer: 'https://via.placeholder.com/200',
        textAnswer: `Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
          Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
          Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`,
        score: 10
      }
    },
    {
      achievement: {
        id: 2,
        title: 'Lorem ipsum2',
        description: 'fdsfdsgsd',
        type: 'TEXT'
      },
      status: 'REJECTED',
      comment: 'nice try tho',
      submission: {
        approved: false,
        textAnswer: `Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
          Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
          Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`,
        score: 0
      }
    },
    {
      achievement: {
        id: 3,
        title: 'Lorem ipsum3',
        description: 'fdsfdsgsd',
        type: 'IMAGE'
      },
      status: 'SUBMITTED',
      submission: {
        approved: false,
        imageUrlAnswer: 'https://via.placeholder.com/150',
        score: 0
      }
    },
    {
      achievement: {
        id: 4,
        title: 'Lorem ipsum4',
        description: 'fdsfdsgsd',
        type: 'BOTH'
      },
      status: 'NOT_SUBMITTED'
    }
  ]
}

export const AchievementList: React.FC<AchievementListProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Bucketlist</Heading>
      <VStack divider={<StackDivider borderColor="gray.200" />} spacing={4} align="stretch">
        {mockData.achievements.map((item) => (
          <Box key={item.achievement.id}>
            <Link to={`/bucketlist/${item.achievement.id}`}>
            <Flex align="center">
              <Text fontSize="lg">{item.achievement.title}</Text>
              <Spacer />
              <AchievementStatusBadge status={item.status} fontSize="sm" />
            </Flex>
            </Link>
          </Box>
        ))}
      </VStack>
    </Page>
  )
}
