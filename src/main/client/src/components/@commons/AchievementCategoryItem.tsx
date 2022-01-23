import { Box, Heading, VStack, StackDivider, Flex, Spacer, Stack, Skeleton, Text } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import React, { useState, useEffect } from 'react'
import axios from 'axios'

import { AchievementStatusBadge } from './AchievementStatusBadge'
import { AchievementCategory, AchievementWrapper } from '../../types/dto/achievements'
import { API_BASE_URL } from 'utils/configurations'

type AchievementCategoryProps = {
  categoryId: number | undefined
  name: string
}

export const AchievementCategoryItem: React.FC<AchievementCategoryProps> = ({ categoryId, name }) => {
  const [achievements, setAchievements] = useState<AchievementWrapper[] | undefined>([])

  useEffect(() => {
    axios.get<AchievementCategory>(`${API_BASE_URL}/api/achievement/category/${categoryId}`).then((res) => {
      setAchievements(res.data.achievements)
    })
  }, [])

  return (
    <Box>
      <Heading size="md">{name}</Heading>
      <VStack divider={<StackDivider borderColor="gray.200" />} spacing={4} align="stretch" marginTop="20px">
        {achievements && achievements.length > 0 ? (
          achievements.map((ach) => (
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
            <Skeleton height="20px" />
            <Skeleton height="20px" />
          </Stack>
        )}
      </VStack>
    </Box>
  )
}
