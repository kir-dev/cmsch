import { GridItem, Heading, Text, Divider } from '@chakra-ui/react'
import { Key } from 'react'
import { Link } from 'react-router-dom'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { NewsPreviewDTO } from '../../../util/views/news.view'

interface NewsListItemProps {
  news: NewsPreviewDTO
  index: Key
}

const NewsListItem = ({ news, index }: NewsListItemProps) => {
  return (
    <>
      <GridItem key={index}>
        <Link to={'/hirek/' + index}>
          <Heading size="md" mt={'3rem'} mb={'0.5rem'} as={news.highlighted ? 'mark' : 'text'}>
            {news.title}
          </Heading>
          <Text>{stringifyTimeStamp(news.timestamp)}</Text>//TODO brief content?
        </Link>
        <Divider />
      </GridItem>
    </>
  )
}

export default NewsListItem
