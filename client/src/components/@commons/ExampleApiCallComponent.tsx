import { Box } from '@chakra-ui/react'
import axios from 'axios'
import { FC, useEffect, useState } from 'react'
import { NewsPreviewDTO, NewsView } from '../../types/dto/news'

export const ExampleApiCallComponent: FC = () => {
  const [newsList, setNewsList] = useState<NewsView>({ warningMessage: '', news: [] })

  useEffect(() => {
    axios.get<NewsView>(`/api/news`).then((res) => {
      console.log(res)
      setNewsList(res.data)
    })
  }, [setNewsList])

  return (
    <Box>
      Result:{' '}
      {newsList?.news.map((item: NewsPreviewDTO) => (
        <Box key={item.timestamp}>{item.title}</Box>
      ))}
    </Box>
  )
}
