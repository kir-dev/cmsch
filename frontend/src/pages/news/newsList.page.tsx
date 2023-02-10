import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import NewsList from './components/NewsList'
import { sortNewsList } from './util/sortNewsList'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

const NewsListPage = () => {
  const newsList = useNewsListQuery(() => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (newsList.isLoading) {
    return <LoadingPage />
  }

  if (newsList.isError) {
    sendMessage(l('news-list-load-failed') + newsList.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof newsList.data === 'undefined') {
    sendMessage(l('news-list-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  return (
    <CmschPage>
      <Helmet title="Hírek" />
      <NewsList newsList={sortNewsList(newsList.data)} />
    </CmschPage>
  )
}

export default NewsListPage
