import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTeamList } from '@/api/hooks/team/queries/useTeamList'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'

import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { SearchBar } from '@/common-components/SearchBar'
import { useSearch } from '@/util/useSearch'
import type { TeamListItemView } from '@/util/views/team.view'
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
  const component = useConfigContext()?.components?.team
  const { data, isLoading, isError } = useTeamList()
  const searchArgs = useSearch<TeamListItemView>(data ?? EmptyData, searchFn)

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage title={component?.title}>
      <h1 className="mb-5 text-4xl font-bold tracking-tight">{component.title}</h1>
      {component.searchEnabled && <SearchBar className="mt-5" {...searchArgs} />}
      <div className="mt-5 flex flex-col gap-4">
        {searchArgs.filteredData?.map((team) => (
          <TeamListItem key={team.id} team={team} detailEnabled={component?.showTeamDetails} />
        ))}
      </div>
    </CmschPage>
  )
}
