import { Grid, Heading } from '@chakra-ui/react'
import { NewsArticleView } from '../../../util/views/news.view'
import NewsListItem from './NewsListItem'

interface NewsListProps {
  newsList: NewsArticleView[]
}

const NewsList = ({ newsList }: NewsListProps) => {
  return (
    <>
      <Heading>Hírek</Heading>
      <Grid templateColumns={{ base: 'repeat(1, auto)', md: 'repeat(2, auto)' }} gap={10} marginTop={10}>
        {newsList.map((n: NewsArticleView, i) => (
          <NewsListItem news={n} fontSize={n.highlighted ? 'lg' : 'md'} index={i} key={n.title + n.timestamp} />
        ))}
      </Grid>
    </>
  )
}

export default NewsList
