import { Box, Heading, StackDivider, VStack, Stack, Text, Flex, Spacer, Skeleton } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'

import { API_BASE_URL } from '../../utils/configurations'
import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'
import {
  AchievementCategory,
  AchievementsInCategory,
  AllAchievementCategories/*,
  AchievementWrapper,
  achievementStatus,
  achievementType*/
} from '../../types/dto/achievements'

type AchievementListProps = {}

// GET /api/achievement -ben lesz egy ilyen tömb
/*const CATEGORIES: AchievementCategory[] = [
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
]*/

// GET /api/achievement/category/1 -ben lesz egy ilyen tömb
/*const ACHIEVEMENTS_IN_CATEGORY: AchievementWrapper[] = [
  {
    achievement: {
      id: 1,
      categoryId: 1,
      title: `Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
        sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.`,
      description: 'blalblal',
      type: achievementType.BOTH
    },
    status: achievementStatus.ACCEPTED
  },
  {
    achievement: {
      id: 2,
      categoryId: 1,
      title: 'Lorem ipsum2',
      description: 'fdsfdsgsd',
      type: achievementType.TEXT
    },
    status: achievementStatus.REJECTED
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
]*/

export const AchievementList: React.FC<AchievementListProps> = (props) => {
  const [categories, setCategories] = useState<AchievementCategory[]>([])

  useEffect(() => {
    axios.get<AllAchievementCategories>(`${API_BASE_URL}/api/achievement`).then((res) => {
      setCategories(res.data.categories)
      categories.forEach((category) => {
        axios.get<AchievementsInCategory>(`${API_BASE_URL}/api/achievement/category/${category.categoryId}`).then((otherRes) => {
          category.achievements = otherRes.data.achievements
        })
      })
    })
  }, [])

  // ez nyilván nem így lesz
  /*CATEGORIES.forEach((category, idx) => {
    if (idx === 0) {
      category.achievements = ACHIEVEMENTS_IN_CATEGORY
    } else {
      category.achievements = []
    }
  })*/

  return (
    <Page {...props} loginRequired>
      <Heading>Bucketlist</Heading>
      <Stack>
        {categories.length > 0 ? (
          categories.map((category) => (
            <Box key={category.categoryId}>
              <Heading size="lg">{category.name}</Heading>
              <VStack divider={<StackDivider borderColor="gray.200" />} spacing={4} align="stretch">
                {category.achievements && category.achievements.length > 0 ? (
                  category.achievements?.map((ach) => (
                    <Box key={ach.achievement.id}>
                      <Link to={`/bucketlist/${ach.achievement.id}`}>
                        <Flex align="center">
                          <Text marginRight="10px" fontSize="lg">
                            {ach.achievement.title}
                          </Text>
                          <Spacer />
                          <AchievementStatusBadge status={ach.status} fontSize="sm" />
                        </Flex>
                      </Link>
                    </Box>
                  ))
                ) : (
                  <Stack marginTop="20px">
                    <Skeleton height="20px"/>
                    <Skeleton height="20px"/>
                  </Stack>
                )}
              </VStack>
            </Box>
          ))
        ) : (
          <Stack marginTop="20px">
            <Skeleton height="40px"/>
            <Skeleton height="40px"/>
            <Skeleton height="40px"/>
            <Skeleton height="40px"/>
            <Skeleton height="40px"/>
          </Stack>
        )}
      </Stack>
    </Page>
  )
}
