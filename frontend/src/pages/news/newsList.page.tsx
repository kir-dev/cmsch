import { Helmet } from 'react-helmet-async'

import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useNewsListQuery } from '../../api/hooks/news/useNewsListQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import NewsList from './components/NewsList'
import { sortNewsList } from './util/sortNewsList'

const NewsListPage = () => {
  const { data, isLoading, isError } = useNewsListQuery()
  const component = useConfigContext()?.components?.news

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component?.title} />

  return (
    <CmschPage>
      <Helmet title={component.title} />
      <NewsList newsList={sortNewsList(data)} />
    </CmschPage>
  )
}

export default NewsListPage
