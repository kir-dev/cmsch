import { Button, Heading, Image, Text } from '@chakra-ui/react'
import { FaArrowLeft } from 'react-icons/fa'
import { Link } from 'react-router'
import { CustomBreadcrumb } from '../../../common-components/CustomBreadcrumb'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp, useBrandColor } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'
import { NewsArticleView } from '../../../util/views/news.view'

interface NewsProps {
  news: NewsArticleView
}

const News = ({ news }: NewsProps) => {
  const brandColor = useBrandColor()
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
      {news.imageUrl && <Image mb={4} display="block" ml="auto" mr="auto" src={news.imageUrl} alt={news.title} maxH="20rem" maxW="full" />}
      <Markdown text={news.content} />
      <Link to={AbsolutePaths.NEWS}>
        <Button leftIcon={<FaArrowLeft />} colorScheme={brandColor} mt={4}>
          Vissza a hírekhez
        </Button>
      </Link>
    </>
  )
}

export default News
