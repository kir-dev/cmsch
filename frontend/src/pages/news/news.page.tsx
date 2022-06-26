import { toInteger } from 'lodash'
import { Helmet } from 'react-helmet'
import { Navigate, useParams } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import News from './components/News'

const NewsPage = () => {
  const news = useNewsListQuery(() => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (news.isError) {
    sendMessage('Hír betöltése sikertelen!')
    return <Navigate replace to="/error" />
  }

  if (news.isSuccess) {
    const params = useParams()
    const id = toInteger(params.id)
    const currentNews = news.data.news[id]

    return (
      <CmschPage>
        <Helmet title={currentNews.title} />
        <News news={currentNews} />
      </CmschPage>
    )
  }

  return <Loading />
}

export default NewsPage
