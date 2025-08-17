import { Grid } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext.tsx'
import { useHomeNews } from '../../../api/hooks/home/useHomeNews.tsx'
import { NewsArticleView } from '../../../util/views/news.view.ts'
import { NewsListItem } from '../../news/components/NewsListItem.tsx'

const sortByHighlighted = (news: NewsArticleView[]) => {
  return news.sort((a, b) => {
    if (a.highlighted && !b.highlighted) {
      return -1
    }
    if (b.highlighted && !a.highlighted) {
      return 1
    }
    return 0
  })
}

export default function HomePageNewsList() {
  const homeNews = useHomeNews()
  const config = useConfigContext()

  if (!homeNews.data || !homeNews.data.length) return null

  return (
    <Grid mt={10} templateColumns="1fr" gap={4}>
      {sortByHighlighted(homeNews.data).map((n: NewsArticleView) => (
        <NewsListItem news={n} fontSize="xl" useLink={config?.components?.news?.showDetails} key={n.title + n.timestamp} />
      ))}
    </Grid>
  )
}
