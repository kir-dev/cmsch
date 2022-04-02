import { Box, Flex, Heading, Skeleton, Stack, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import axios from 'axios'
import { CustomBreadcrumb } from 'components/@commons/CustomBreadcrumb'
import { Page } from 'components/@layout/Page'
import { FC, useEffect, useState } from 'react'
import { Helmet } from 'react-helmet'
import { Link, useParams } from 'react-router-dom'
import { Loading } from 'utils/Loading'
import { AchievementCategory } from '../../types/dto/achievements'
import { useServiceContext } from '../../utils/useServiceContext'
import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'

export const AchievementCategoryPage: FC = (props) => {
  const { throwError } = useServiceContext()
  const [category, setCategory] = useState<AchievementCategory | undefined>(undefined)
  const { id } = useParams()

  useEffect(() => {
    axios
      .get<AchievementCategory>(`/api/achievement/category/${id}`)
      .then((res) => {
        setCategory(res.data)
      })
      .catch(() => {
        throwError('Nem sikerült lekérdezni a Bucketlist feladatokat ebben a kategóriában.')
      })
  }, [])

  if (!category) {
    return (
      <Loading>
        <Stack marginTop="20px">
          <Skeleton height="20px" />
          <Skeleton height="20px" />
        </Stack>
      </Loading>
    )
  }

  const breadcrumbItems = [
    {
      title: 'Bucketlist',
      to: '/bucketlist'
    },
    {
      title: category.categoryName
    }
  ]

  return (
    <Page {...props} loginRequired groupRequired>
      <Helmet title={category.categoryName} />
      <CustomBreadcrumb items={breadcrumbItems} />
      <Heading>Bucketlist kategória: {category.categoryName}</Heading>
      {category.achievements && category.achievements.length > 0 ? (
        <VStack spacing={4} mt={5} align="stretch">
          {category.achievements.map((ach) => (
            <Box
              key={ach.achievement.id}
              bg={useColorModeValue('gray.200', 'gray.600')}
              px={6}
              py={2}
              borderRadius="md"
              _hover={{ bgColor: useColorModeValue('brand.300', 'brand.700') }}
            >
              <Link to={`/bucketlist/${ach.achievement.id}`}>
                <Flex align="center" justifyContent="space-between">
                  <Text fontWeight="bold" fontSize="xl">
                    {ach.achievement.title}
                  </Text>
                  <AchievementStatusBadge status={ach.status} fontSize="sm" />
                </Flex>
              </Link>
            </Box>
          ))}
        </VStack>
      ) : (
        <Text>Nincs egyetlen bucketlist feladat se ebben a kategóriában.</Text>
      )}
    </Page>
  )
}
