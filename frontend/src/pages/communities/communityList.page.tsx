import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useCommunityList } from '@/api/hooks/community/useCommunityList'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown.tsx'
import { PageStatus } from '@/common-components/PageStatus'
import { Input } from '@/components/ui/input'
import { AbsolutePaths } from '@/util/paths'
import type { Community } from '@/util/views/organization'
import { Search } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { CardListItem } from './components/CardListItem'

export default function CommunityListPage() {
  const communities = useConfigContext()?.components?.communities
  const { data, isLoading, isError } = useCommunityList()
  const [filteredCommunities, setFilteredCommunities] = useState<Community[]>(data || [])
  const inputRef = useRef<HTMLInputElement>(null)

  const handleInput = () => {
    const search = inputRef.current?.value?.toLowerCase() || ''
    if (!data) {
      setFilteredCommunities([])
    } else if (!search) setFilteredCommunities(data)
    else
      setFilteredCommunities(
        data.filter((c) => {
          if (c.searchKeywords?.find((s) => s.toLowerCase().includes(search))) return true
          if (c.interests?.find((i) => i.toLowerCase().includes(search))) return true
          return c.name.toLowerCase().includes(search)
        })
      )
  }

  useEffect(() => {
    if (data) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setFilteredCommunities(data)
      if (inputRef.current) inputRef.current.value = ''
    }
  }, [data])

  if (!communities) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={communities?.title} />

  return (
    <CmschPage title={communities?.title}>
      <h1 className="text-3xl font-bold font-heading">{communities?.title}</h1>
      <div className="relative mt-5 flex items-center">
        <Search className="absolute left-3 h-5 w-5 text-muted-foreground" />
        <Input ref={inputRef} placeholder="Keresés..." className="pl-10 h-12 text-lg" onChange={handleInput} autoFocus={true} />
      </div>
      {communities?.description && (
        <div className="mt-5">
          <Markdown text={communities?.description} />
        </div>
      )}
      <div className="mt-5">
        {filteredCommunities?.map((community) => (
          <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITY}/${community.id}`} />
        ))}
      </div>
    </CmschPage>
  )
}
