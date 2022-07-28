import { Helmet } from 'react-helmet-async'
import { Navigate, useParams } from 'react-router-dom'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import News from './components/News'
import { AbsolutePaths } from '../../util/paths'
import { useNewsQuery } from '../../api/hooks/useNewsQuery'

const NewsPage = () => {
  const { id } = useParams()
  const { data, isLoading, isError } = useNewsQuery(id || '', () => console.log('News list query failed!'))
  const { sendMessage } = useServiceContext()

  if (isLoading) {
    return <Loading />
  }

  if (isError) {
    sendMessage('Hír betöltése sikertelen!')
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof data === 'undefined') {
    sendMessage('Hír betöltése sikertelen!\n Keresse az oldal fejlesztőit.')
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
