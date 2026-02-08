import { Heading } from '@chakra-ui/react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTeamList } from '../../api/hooks/team/queries/useTeamList'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { SearchBar } from '../../common-components/SearchBar'
import { useSearch } from '../../util/useSearch'
import { TeamListItemView } from '../../util/views/team.view'
import { TeamListItem } from './components/TeamListItem'

const EmptyData: TeamListItemView[] = []
const searchFn = (item: TeamListItemView, search: string) => {
  return (
    (item.name.toLowerCase().includes(search) ||
      item.labels?.some((label) => {
        return label.name.toLowerCase().includes(search)
      })) ??
    false
  )
}

export default function TeamListPage() {
  const config = useConfigContext()
  const component = config?.components?.team
  const app = config?.components?.app
  const { data, isLoading, isError } = useTeamList()
  const searchArgs = useSearch<TeamListItemView>(data ?? EmptyData, searchFn)

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage>
      <title>
        {app?.siteName || 'CMSch'} | {component?.title}
      </title>
      <Heading as="h1" variant="main-title">
        {component.title}
      </Heading>
      {component.searchEnabled && <SearchBar mt={5} {...searchArgs} />}
      {searchArgs.filteredData?.map((team) => (
        <TeamListItem key={team.id} team={team} detailEnabled={component?.showTeamDetails} />
      ))}
    </CmschPage>
  )
}
