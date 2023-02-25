import { SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputGroup, InputLeftElement } from '@chakra-ui/react'
import { createRef, useEffect, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Paragraph } from '../../common-components/Paragraph'
import { CardListItem } from './components/CardListItem'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Community } from '../../util/views/organization'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'
import { useCommunityList } from '../../api/hooks/community/useCommunityList'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { PageStatus } from '../../common-components/PageStatus'

export default function CommunityListPage() {
  const config = useConfigContext()?.components.communities
  const { data, isLoading, isError } = useCommunityList()
  const [filteredCommunities, setFilteredCommunities] = useState<Community[]>(data || [])
  const inputRef = createRef<HTMLInputElement>()

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!data) {
      setFilteredCommunities([])
    } else if (!search) setFilteredCommunities(data)
    else
      setFilteredCommunities(
        data.filter((c) => {
          if (c.searchKeywords?.find((s) => s.toLowerCase().includes(search))) return true
          if (c.interests?.find((i) => i.toLowerCase().includes(search))) return true
          return c.name.toLocaleLowerCase().includes(search)
        })
      )
  }

  useEffect(() => {
    if (data) {
      setFilteredCommunities(data)
      if (inputRef.current) inputRef.current.value = ''
    }
  }, [data])

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={config?.title} />

  return (
    <CmschPage>
      <Helmet title={l('community-title')} />
      <Heading>{l('community-title')}</Heading>
      <InputGroup mt={5}>
        <InputLeftElement h="100%">
          <SearchIcon />
        </InputLeftElement>
        <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
      </InputGroup>
      <Paragraph>{l('community-description')}</Paragraph>
      {filteredCommunities?.map((community) => (
        <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITY}/${community.id}`} />
      ))}
    </CmschPage>
  )
}
