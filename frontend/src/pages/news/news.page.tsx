import { useNewsQuery } from '@/api/hooks/news/useNewsQuery'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { useParams } from 'react-router'
import News from './components/News'

const NewsPage = () => {
  const { id } = useParams()
  const { data, isLoading, isError } = useNewsQuery(id || 'UNKNOWN')

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Hír" />

  return (
    <CmschPage title={data.title}>
      <News news={data} />
    </CmschPage>
  )
}

export default NewsPage
