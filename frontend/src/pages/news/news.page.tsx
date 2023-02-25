import { Helmet } from 'react-helmet-async'
import { useParams } from 'react-router-dom'
import { CmschPage } from '../../common-components/layout/CmschPage'
import News from './components/News'
import { useNewsQuery } from '../../api/hooks/news/useNewsQuery'
import { PageStatus } from '../../common-components/PageStatus'

const NewsPage = () => {
  const { id } = useParams()
  const { data, isLoading, isError } = useNewsQuery(id || 'UNKNOWN')

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Hír" />

  return (
    <CmschPage>
      <Helmet title={data.title} />
      <News news={data} />
    </CmschPage>
  )
}

export default NewsPage
