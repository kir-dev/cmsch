import { useParams } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useNewsQuery } from '../../api/hooks/news/useNewsQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import News from './components/News'

const NewsPage = () => {
  const { id } = useParams()
  const { data, isLoading, isError } = useNewsQuery(id || 'UNKNOWN')
  const appComponent = useConfigContext()?.components?.app

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Hír" />

  return (
    <CmschPage>
      <title>
        {appComponent?.siteName || 'CMSch'} | {data.title}
      </title>{' '}
      <News news={data} />
    </CmschPage>
  )
}

export default NewsPage
