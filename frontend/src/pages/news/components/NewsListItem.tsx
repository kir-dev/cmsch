import { Divider, GridItem, Heading, Text } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { NewsArticleView } from '../../../util/views/news.view'
import { AbsolutePaths } from '../../../util/paths'
import Markdown from "../../../common-components/Markdown";

interface NewsListItemProps {
  news: NewsArticleView
  fontSize: string
  index: Number
}

const NewsListItem = ({ news, fontSize, index }: NewsListItemProps) => {
  return (
    <GridItem>
      <Link to={`${AbsolutePaths.NEWS}/${news.url}`}>
        <Heading size={fontSize} mt={'2rem'} mb={'0.5rem'}>
          {news.title}
        </Heading>
        <Text fontSize={fontSize}>{stringifyTimeStamp(news.timestamp)}</Text>
        <Markdown text={news.briefContent || ''} />
      </Link>
      <Divider />
    </GridItem>
  )
}

export default NewsListItem
