import { Helmet } from 'react-helmet'
import { Navigate } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import NewsList from './components/NewsList'
import { sortNewsList } from './util/sortNewsList'

const NewsListPage = () => {
  const newsList = useNewsListQuery(() => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (newsList.isLoading) {
    return <Loading />
  }

  if (newsList.isError) {
    sendMessage('Hírek betöltése sikertelen!\n' + newsList.error.message)
    return <Navigate replace to="/error" />
  }

  if (typeof newsList.data === 'undefined') {
    sendMessage('Hírek betöltése sikertelen!\n Keresd az oldal fejlesztőit.')
    return <Navigate replace to="/error" />
  }

  return (
    <CmschPage>
      <Helmet title="Hírek" />
      <NewsList newsList={sortNewsList(newsList.data)} />
    </CmschPage>
  )
}

export default NewsListPage
