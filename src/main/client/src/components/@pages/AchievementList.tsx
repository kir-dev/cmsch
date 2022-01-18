import { Box, Heading, StackDivider, VStack, Stack, Text, Flex, Spacer } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { Link } from 'react-router-dom'

import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'
import { AchievementCategory, AchievementWrapper, achievementStatus, achievementType } from '../../types/dto/achievements'

type AchievementListProps = {}

// GET /api/achievement -ben lesz egy ilyen tömb
const CATEGORIES: AchievementCategory[] = [
  {
    categoryId: 1,
    name: 'első kategória'
  },
  {
    categoryId: 2,
    name: 'második kategória'
  },
  {
    categoryId: 3,
    name: 'századik kategória'
  }
]

// GET /api/achievement/category/1 -ben lesz egy ilyen tömb
const ACHIEVEMENTS_IN_CATEGORY: AchievementWrapper[] = [
  {
    achievement: {
      id: 1,
      categoryId: 1,
      title: `Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
        sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.`,
      description: 'blalblal',
      type: achievementType.BOTH
    },
    status: achievementStatus.ACCEPTED,
    comment: 'Nice work!'
  },
  {
    achievement: {
      id: 2,
      categoryId: 1,
      title: 'Lorem ipsum2',
      description: 'fdsfdsgsd',
      type: achievementType.TEXT
    },
    status: achievementStatus.REJECTED,
    comment: 'nice try tho'
  },
  {
    achievement: {
      id: 3,
      categoryId: 1,
      title: 'Lorem ipsum3',
      description: 'fdsfdsgsd',
      type: achievementType.IMAGE
    },
    status: achievementStatus.SUBMITTED
  },
  {
    achievement: {
      id: 4,
      categoryId: 1,
      title: 'Lorem ipsum4',
      description: 'fdsfdsgsd',
      type: achievementType.BOTH
    },
    status: achievementStatus.NOT_SUBMITTED
  }
]

export const AchievementList: React.FC<AchievementListProps> = (props) => {
  // ez nyilván nem így lesz
  CATEGORIES.forEach((category, idx) => {
    if (idx === 0) {
      category.achievements = ACHIEVEMENTS_IN_CATEGORY
    } else {
      category.achievements = []
    }
  })

  return (
    <Page {...props} loginRequired>
      <Heading>Bucketlist</Heading>
      <Stack>
        {CATEGORIES.map((category) => (
          <>
            <Heading size="lg">{category.name}</Heading>
            <VStack divider={<StackDivider borderColor="gray.200" />} spacing={4} align="stretch">
              {category.achievements?.map((ach) => (
                <Box key={ach.achievement.id}>
                  <Link to={`/bucketlist/${ach.achievement.id}`}>
                    <Flex align="center">
                      <Text fontSize="lg">{ach.achievement.title}</Text>
                      <Spacer />
                      <AchievementStatusBadge status={ach.status} fontSize="sm" />
                    </Flex>
                  </Link>
                </Box>
              ))}
            </VStack>
          </>
        ))}
      </Stack>
    </Page>
  )
}
