import { Helmet } from 'react-helmet'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Navigate } from 'react-router-dom'
import NewsList from './components/NewsList'
import { Loading } from '../../common-components/Loading'

const NewsListPage = () => {
  const newsList = useNewsListQuery(() => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (newsList.isError) {
    sendMessage('Hírek betöltése sikertelen!')
    return <Navigate replace to="/error" />
  }

  if (newsList.isSuccess) {
    return (
      <CmschPage>
        <Helmet title="Hírek" />
        <NewsList newsList={newsList.data.news} warningMessage={newsList.data.warningMessage} />
      </CmschPage>
    )
  }

  return <Loading />
}

export default NewsListPage
