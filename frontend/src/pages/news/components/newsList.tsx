import { Heading, Text, Grid } from '@chakra-ui/react'
import { NewsPreviewDTO } from '../../../util/views/news.view'
import NewsListItem from './newsListItem'

interface NewsListProps {
  newsList: NewsPreviewDTO[]
  warningMessage: String
}

const NewsList = ({ newsList, warningMessage }: NewsListProps) => {
  return (
    <>
      <Heading>Hírek</Heading>
      <Text>{warningMessage}</Text>
      <Grid templateColumns={{ base: 'repeat(1, auto)', md: 'repeat(2, auto)' }} gap={10} marginTop={10}>
        {newsList.map((n: NewsPreviewDTO, i) => (
          <NewsListItem news={n} index={i} />
        ))}
      </Grid>
    </>
  )
}

export default NewsList
