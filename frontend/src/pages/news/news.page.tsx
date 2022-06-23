import { Heading } from '@chakra-ui/react'
import { toInteger } from 'lodash'
import { Helmet } from 'react-helmet'
import { useParams } from 'react-router-dom'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'

const NewsPage = () => {
  const news = useNewsListQuery(() => console.log('kill me'))
  const params = useParams()

  const id = params.id ? params.id : 0

  const currentNews = news.data?.news[toInteger(id)]

  return (
    <CmschPage>
      <Helmet title="Hír" />
      <Heading mb="1rem">{currentNews?.title}</Heading>
      <Markdown text={currentNews?.content} />
    </CmschPage>
  )
}

export default NewsPage
