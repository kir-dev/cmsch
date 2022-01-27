import { Flex, Heading, Stack, VStack, Box, Skeleton, Text, useColorModeValue } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React, { useEffect, useState } from 'react'
import axios from 'axios'

import { API_BASE_URL } from '../../utils/configurations'
import { AchievementCategory, AllAchievementCategories } from '../../types/dto/achievements'
import { Loading } from '../../utils/Loading'
import { useServiceContext } from '../../utils/useServiceContext'
import { Link } from 'react-router-dom'
import { Helmet } from 'react-helmet'

const progress = (category: AchievementCategory) => {
  return (category.approved + category.notGraded) / category.sum
}

const progressGradient = (progress: number, color: string) => {
  const endDeg = 360 * progress
  if (progress === 1) {
    return `conic-gradient(${color} 0deg, ${color} 360deg)`
  }
  if (progress === 0) {
    return `conic-gradient(grey 0deg, gray 360deg)`
  }
  return `conic-gradient(grey 0deg,${color} 10deg, ${color} ${endDeg}deg, grey ${endDeg + 10}deg)`
}

export const AchievementCategoryList: React.FC = (props) => {
  const bg = useColorModeValue('gray.200', 'gray.600')
  const [categories, setCategories] = useState<AchievementCategory[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const { throwError } = useServiceContext()

  useEffect(() => {
    axios
      .get<AllAchievementCategories>(`${API_BASE_URL}/api/achievement`)
      .then((res) => {
        setCategories(res.data.categories)
        setLoading(false)
      })
      .catch(() => {
        throwError('Nem sikerült lekérdezni a Bucketlist feladatokat.')
      })
  }, [])

  if (loading)
    return (
      <Loading>
        {[0, 1, 2].map((idx) => (
          <Stack key={idx} mt="20px">
            <Skeleton height="40px" />
            <Skeleton height="20px" />
            <Skeleton height="20px" />
          </Stack>
        ))}
      </Loading>
    )
  return (
    <Page {...props} loginRequired>
      <Helmet title="Bucketlist kategóriák" />
      <Heading>Bucketlist</Heading>
      {categories.length > 0 ? (
        <VStack spacing={4} mt={5} align="stretch">
          {categories.map((category) => (
            <Box bg={bg} px={6} py={2} borderRadius="md" _hover={{ bgColor: useColorModeValue('brand.300', 'brand.700') }}>
              <Link to={`/bucketlist/kategoria/${category.categoryId}`}>
                <Flex align="center" justifyContent="space-between">
                  <Text fontWeight="bold" fontSize="xl">
                    {category.name}
                  </Text>
                  <Box bgGradient={progressGradient(progress(category), 'brand.600')} px={1} py={1} borderRadius="6px">
                    <Text bg={bg} px={4} py={2} borderRadius="6px" fontWeight="bold">
                      {category.approved + category.notGraded} / {category.sum}
                    </Text>
                  </Box>
                </Flex>
              </Link>
            </Box>
          ))}
        </VStack>
      ) : (
        <Text>Nincs egyetlen bucketlist challenge se.</Text>
      )}
    </Page>
  )
}
