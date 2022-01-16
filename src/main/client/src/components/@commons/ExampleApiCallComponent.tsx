import * as React from 'react'
import { Box } from '@chakra-ui/react'
import axios from 'axios'
import { API_BASE_URL } from '../../utils/configurations'
import { NewsPreviewDTO, NewsView } from '../../types/dto/news'

export const ExampleApiCallComponent: React.FC = () => {
  const [newsList, setNewsList] = React.useState<NewsView>({ warningMessage: '', news: [] })

  React.useEffect(() => {
    axios.get<NewsView>(`${API_BASE_URL}/api/news`).then((res) => {
      console.log(res)
      setNewsList(res.data)
    })
  }, [setNewsList])

  return (
    <Box>
      Result:{' '}
      {newsList?.news.map((item: NewsPreviewDTO) => (
        <Box>{item.title}</Box>
      ))}
    </Box>
  )
}
