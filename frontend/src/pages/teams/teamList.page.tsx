import { Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTeamList } from '../../api/hooks/team/queries/useTeamList'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { SearchBar } from '../../common-components/SearchBar'
import { useSearch } from '../../util/useSearch'
import { TeamListItemView } from '../../util/views/team.view'
import { TeamListItem } from './components/TeamListItem'

export default function TeamListPage() {
  const config = useConfigContext()
  const component = config?.components.team
  const { data, isLoading, isError } = useTeamList()
  const searchArgs = useSearch<TeamListItemView>(data ?? [], (item, search) => item.name.toLowerCase().includes(search))

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage>
      <Helmet title={component.title} />
      <Heading>{component.title}</Heading>
      <SearchBar mt={5} {...searchArgs} />
      {searchArgs.filteredData?.map((team) => (
        <TeamListItem key={team.id} team={team} detailEnabled={component?.showTeamDetails} />
      ))}
    </CmschPage>
  )
}
