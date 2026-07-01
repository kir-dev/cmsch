import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useOrganizationList } from '@/api/hooks/community/useOrganizationList'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown.tsx'
import { PageStatus } from '@/common-components/PageStatus'
import { Input } from '@/components/ui/input'
import { AbsolutePaths } from '@/util/paths'
import type { Organization } from '@/util/views/organization'
import { Search } from 'lucide-react'
import { createRef, useEffect, useState } from 'react'
import { CardListItem } from './components/CardListItem'

export default function OrganizationListPage() {
  const communities = useConfigContext()?.components?.communities
  const { data, isLoading, isError } = useOrganizationList()
  const [filteredOrganizations, setFilteredOrganizations] = useState<Organization[]>(data || [])
  const inputRef = createRef<HTMLInputElement>()

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!data) setFilteredOrganizations([])
    else if (!search) setFilteredOrganizations(data)
    else
      setFilteredOrganizations(
        data.filter((o) => {
          if (o.interests?.find((i) => i.toLowerCase().includes(search))) return true
          return o.name.toLocaleLowerCase().includes(search)
        })
      )
  }

  useEffect(() => {
    if (data) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setFilteredOrganizations(data)
      if (inputRef.current) inputRef.current.value = ''
    }
  }, [data])

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={communities?.title} />

  return (
    <CmschPage title={communities?.titleResort}>
      <h1 className="text-3xl font-bold font-heading">{communities?.titleResort}</h1>
      <div className="relative mt-5 flex items-center">
        <Search className="absolute left-3 h-5 w-5 text-muted-foreground" />
        <Input ref={inputRef} placeholder="Keresés..." className="pl-10 h-12 text-lg" onChange={handleInput} autoFocus={true} />
      </div>
      {communities?.descriptionResort && (
        <div className="mt-5">
          <Markdown text={communities?.descriptionResort} />
        </div>
      )}
      <div className="mt-5">
        {filteredOrganizations.map((organization) => (
          <CardListItem key={organization.id} data={organization} link={`${AbsolutePaths.ORGANIZATION}/${organization.id}`} />
        ))}
      </div>
    </CmschPage>
  )
}
