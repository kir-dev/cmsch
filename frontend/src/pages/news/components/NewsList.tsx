import { Grid, Heading, useBreakpointValue } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { NewsArticleView } from '../../../util/views/news.view'
import NewsListItem from './NewsListItem'

interface NewsListProps {
  newsList: NewsArticleView[]
}

const NewsList = ({ newsList }: NewsListProps) => {
  const highlighted = newsList.filter((news) => news.highlighted).sort((a, b) => b.timestamp - a.timestamp) // desc
  const config = useConfigContext()
  return (
    <>
      <Heading>{config?.components.news.title}</Heading>
      <Grid templateColumns="repeat(1, 1fr)" gap={4} marginTop={4}>
        {highlighted.map((n: NewsArticleView, i) => (
          <NewsListItem news={n} fontSize="2xl" index={i} key={n.title + n.timestamp} />
        ))}
      </Grid>
      <Grid
        templateColumns={`repeat(${useBreakpointValue({ base: 1, md: 1 })}, 1fr)`}
        gap={4}
        marginTop={highlighted.length === 0 ? 4 : 20}
      >
        {newsList
          .filter((news) => !news.highlighted)
          .sort((a, b) => b.timestamp - a.timestamp) // desc
          .map((n: NewsArticleView, i) => (
            <NewsListItem news={n} fontSize="xl" index={i} key={n.title + n.timestamp} />
          ))}
      </Grid>
    </>
  )
}

export default NewsList
