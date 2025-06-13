import { SearchIcon } from '@chakra-ui/icons'
import { Box, Heading, Input, InputGroup, InputLeftElement } from '@chakra-ui/react'
import { createRef, useEffect, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useCommunityList } from '../../api/hooks/community/useCommunityList'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { AbsolutePaths } from '../../util/paths'
import { Community } from '../../util/views/organization'
import { CardListItem } from './components/CardListItem'
import Markdown from '../../common-components/Markdown.tsx'

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
      <Helmet title={config?.title} />
      <Heading as="h1" variant="main-title">
        {config?.title}
      </Heading>
      <InputGroup mt={5}>
        <InputLeftElement h="100%">
          <SearchIcon />
        </InputLeftElement>
        <Input
          ref={inputRef}
          placeholder="Keresés..."
          size="lg"
          onChange={handleInput}
          _placeholder={{ color: 'inherit' }}
          autoFocus={true}
        />
      </InputGroup>
      {config?.description && (
        <Box mt={5}>
          <Markdown text={config?.description} />
        </Box>
      )}
      {filteredCommunities?.map((community) => (
        <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITY}/${community.id}`} />
      ))}
    </CmschPage>
  )
}
