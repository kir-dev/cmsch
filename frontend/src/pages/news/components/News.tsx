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
      <Heading>{news.title}</Heading>
      <Text mb="1rem">{stringifyTimeStamp(news.timestamp)}</Text>
      <Image
        mb="1.5rem"
        display="block"
        ml="auto"
        mr="auto"
        src={news.imageUrl == '' ? 'https://picsum.photos/200' : news.imageUrl} //TODO random képet kivenni
        placeholder="ide kéne kép"
        h="20rem"
      />
      <Markdown text={news.content} />
      <Link to={AbsolutePaths.NEWS}>
        <Button mt="2rem">Vissza a hírekhez</Button>
      </Link>
    </>
  )
}

export default News
