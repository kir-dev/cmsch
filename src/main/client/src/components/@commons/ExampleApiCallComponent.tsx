import * as React from 'react'
import { Box } from '@chakra-ui/react'
import axios from 'axios'

export const ExampleApiCallComponent: React.FC = () => {
  const [newsList, setNewsList] = React.useState<any>([])

  React.useEffect(() => {
    axios.get(`/api/news`).then((res) => {
      setNewsList(res.data)
    })
  }, newsList)

  return (
    <Box>
      {newsList.map((newsItem: any) => (
        <Box>{newsItem.toString()}</Box>
      ))}
    </Box>
  )
}
