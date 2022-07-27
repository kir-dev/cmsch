import { Button, Heading, Image, Text } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { NewsArticleView } from '../../../util/views/news.view'
import { AbsolutePaths } from '../../../util/paths'
import { CustomBreadcrumb } from '../../../common-components/CustomBreadcrumb'

interface NewsProps {
  news: NewsArticleView
}

const News = ({ news }: NewsProps) => {
  const breadcrumbItems = [
    {
      title: 'Hírek',
      to: AbsolutePaths.NEWS
    },
    {
      title: news.title
    }
  ]
  return (
    <>
      <CustomBreadcrumb items={breadcrumbItems} />
      <Text fontSize="xs" fontWeight={300} textAlign="end">
        {stringifyTimeStamp(news.timestamp)}
      </Text>
      <Heading mb={2}>{news.title}</Heading>
      {news.imageUrl && news.imageUrl !== '' && (
        <Image mb={4} display="block" ml="auto" mr="auto" src={news.imageUrl} alt={news.title} maxH="20rem" />
      )}
      <Markdown text={news.content} />
      <Link to={AbsolutePaths.NEWS}>
        <Button mt={4}>Vissza a hírekhez</Button>
      </Link>
    </>
  )
}

export default News
