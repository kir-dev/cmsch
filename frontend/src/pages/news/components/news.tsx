import { Heading, Text, Image, Button } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { NewsPreviewDTO } from '../../../util/views/news.view'

interface NewsProps {
  news: NewsPreviewDTO
}

const News = ({ news }: NewsProps) => {
  return (
    <>
      <Heading>{news.title}</Heading>
      <Text mb="1rem">{stringifyTimeStamp(news.timestamp)}</Text>
      <Markdown text={news.content} />
      <Image
        mt="2rem"
        mb="2rem"
        display="block"
        ml="auto"
        mr="auto"
        src={news.imageUrl == '' ? 'https://picsum.photos/200' : news.imageUrl} //TODO random képet kivenni
        placeholder="ide kéne kép"
        w="20rem"
      />
      <Link to="/hirek">
        <Button>Vissza a hírekhez</Button>
      </Link>
    </>
  )
}

export default News
