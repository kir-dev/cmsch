import { Button, Heading, Image, Text } from '@chakra-ui/react'
import { toInteger } from 'lodash'
import { Helmet } from 'react-helmet'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { stringifyTimeStamp } from '../../util/core-functions.util'

const NewsPage = () => {
  const news = useNewsListQuery(() => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (news.isSuccess) {
    const params = useParams()
    const id = toInteger(params.id)
    const currentNews = news.data.news[id]

    return (
      <CmschPage>
        <Helmet title={currentNews.title} />
        <Heading>{currentNews.title}</Heading>
        <Text mb="1rem">{stringifyTimeStamp(currentNews.timestamp)}</Text>
        <Markdown text={currentNews.content} />
        <Image
          mt="2rem"
          mb="2rem"
          display="block"
          ml="auto"
          mr="auto"
          src={currentNews.imageUrl == '' ? 'https://picsum.photos/200' : currentNews.imageUrl} //TODO random képet kivenni
          placeholder="ide kéne kép"
          w="20rem"
        />
        <Link to="/hirek">
          <Button>Vissza a hírekhez</Button>
        </Link>
      </CmschPage>
    )
  } else {
    sendMessage('Hír betöltése sikertelen!')
    return <Navigate replace to="/error" />
  }
}

export default NewsPage
