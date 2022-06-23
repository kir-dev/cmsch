import { Grid, Heading, Text, Box } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import NewsList from './components/newsListItem'
import { Link, Navigate } from 'react-router-dom'
import Markdown from '../../common-components/Markdown'

const NewsListPage = () => {
  const newsList = useNewsListQuery(() => console.log('kill me'))
  const { sendMessage } = useServiceContext()

  if (typeof newsList.data === undefined) {
    sendMessage('Hírek betöltése sikertelen! ')
    return <Navigate replace to="/error" />
  }

  //<NewsList newsList={newsList.data.news} />

  return (
    <CmschPage>
      <Helmet title="Hírek" />
      <Heading>News list</Heading>
      <Text>{newsList.data?.warningMessage}</Text>
      {newsList.data?.news.map((n, i) => (
        <Box key={i}>
          <Box mt={'3rem'} mb={'0.5rem'} as={n.highlighted ? 'mark' : 'text'}>
            <Link to={'/hirek/' + i}>{n.title}</Link>
          </Box>
          <Markdown text={n.timestamp.toLocaleString()} />
        </Box>
      ))}
    </CmschPage>
  )
}

export default NewsListPage
