import { Heading, Stack, Skeleton } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React, { useEffect, useState } from 'react'
import axios from 'axios'

import { API_BASE_URL } from '../../utils/configurations'
import { AchievementCategory, AllAchievementCategories } from '../../types/dto/achievements'
import { AchievementCategoryItem } from '../@commons/AchievementCategoryItem'

export const AchievementList: React.FC = (props) => {
  const [categories, setCategories] = useState<AchievementCategory[]>([])

  useEffect(() => {
    axios.get<AllAchievementCategories>(`${API_BASE_URL}/api/achievement`).then((res) => {
      setCategories(res.data.categories)
    })
  }, [])

  return (
    <Page {...props} loginRequired>
      <Heading>Bucketlist</Heading>
      <Stack>
        {categories.length > 0
          ? categories.map((category) => (
              <AchievementCategoryItem key={category.categoryId} categoryId={category.categoryId} name={category.name} />
            ))
          : [0, 1, 2].map((idx) => (
              <Stack key={idx} marginTop="20px">
                <Skeleton height="40px" />
                <Skeleton height="20px" />
                <Skeleton height="20px" />
              </Stack>
            ))}
      </Stack>
    </Page>
  )
}
