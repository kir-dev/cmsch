import { toInteger } from 'lodash'
import { Helmet } from 'react-helmet'
import { Navigate, useParams } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useNewsListQuery } from '../../api/hooks/useNewsListQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import News from './components/News'

const NewsPage = () => {
  const newsList = useNewsListQuery(() => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()
  const params = useParams()

  if (newsList.isLoading) {
    return <Loading />
  }

  if (newsList.isError) {
    sendMessage('Hír betöltése sikertelen!')
    return <Navigate replace to="/error" />
  }

  if (typeof newsList.data === 'undefined') {
    sendMessage('Hír betöltése sikertelen!\n Keresd az oldal fejlesztőit.')
    return <Navigate replace to="/error" />
  }

  const id = toInteger(params.id)
  const currentNews = newsList.data[id]

  return (
    <CmschPage>
      <Helmet title={currentNews.title} />
      <News news={currentNews} />
    </CmschPage>
  )
}

export default NewsPage
