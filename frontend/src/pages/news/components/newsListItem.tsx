import { Divider, GridItem, Heading, Text } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { NewsArticleView } from '../../../util/views/news.view'

interface NewsListItemProps {
  news: NewsArticleView
  index: Number
}

const NewsListItem = ({ news, index }: NewsListItemProps) => {
  return (
    <GridItem>
      <Link to={'/hirek/' + index}>
        <Heading size="md" mt={'3rem'} mb={'0.5rem'} as={news.highlighted ? 'mark' : 'text'}>
          {news.title}
        </Heading>
        <Text>{stringifyTimeStamp(news.timestamp)}</Text>//TODO brief content?
      </Link>
      <Divider />
    </GridItem>
  )
}

export default NewsListItem
