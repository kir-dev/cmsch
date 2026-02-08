import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useNewsListQuery } from '../../api/hooks/news/useNewsListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import NewsList from './components/NewsList'
import { sortNewsList } from './util/sortNewsList'

const NewsListPage = () => {
  const { data, isLoading, isError } = useNewsListQuery()
  const config = useConfigContext()?.components
  const component = config?.news
  const app = config?.app

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component?.title} />

  return (
    <CmschPage>
      <title>
        {app?.siteName || 'CMSch'} | {component?.title}
      </title>
      <NewsList newsList={sortNewsList(data)} />
    </CmschPage>
  )
}

export default NewsListPage
