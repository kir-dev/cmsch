import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import News from './components/News'
import { AbsolutePaths } from '../../util/paths'
import { useNewsQuery } from '../../api/hooks/news/useNewsQuery'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

const NewsPage = () => {
  const { id } = useParams()
  const { data, isLoading, isError } = useNewsQuery(id || '', () => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (isLoading) {
    return <LoadingPage />
  }

  if (isError) {
    sendMessage(l('news-item-load-failed'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof data === 'undefined') {
    sendMessage(l('news-item-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  return (
    <CmschPage>
      <Helmet title={data.title} />
      <News news={data} />
    </CmschPage>
  )
}

export default NewsPage
