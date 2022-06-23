import { Grid, GridItem, useColorModeValue, Text } from '@chakra-ui/react'
import { NewsPreviewDTO } from '../../../util/views/news.view'

type NewsListProps = {
  newsList: NewsPreviewDTO[]
}

const NewsList = ({ newsList }: NewsListProps) => (
  <Grid templateColumns="repeat(2, auto)" gap={10} marginTop={10}>
    {newsList.map((n) => (
      <NewsListItem news={n} />
    ))}
  </Grid>
)

type NewsDisplayProps = {
  news: NewsPreviewDTO
}

const NewsListItem = ({ news }: NewsDisplayProps) => {
  return (
    <>
      <GridItem textAlign="right">
        <Text fontSize="2xl" color={useColorModeValue('brand.500', 'brand.600')}>
          {news.title}
        </Text>
      </GridItem>
      <GridItem>
        <Text fontSize="2xl">{news.content}</Text>
      </GridItem>
    </>
  )
}

export default NewsList
