import { SearchIcon } from '@chakra-ui/icons'
import { Box, Heading, Input, InputGroup, InputLeftElement } from '@chakra-ui/react'
import { createRef, useEffect, useState } from 'react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useOrganizationList } from '../../api/hooks/community/useOrganizationList'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown.tsx'
import { PageStatus } from '../../common-components/PageStatus'
import { AbsolutePaths } from '../../util/paths'
import type { Organization } from '../../util/views/organization'
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
  }, [data, inputRef])

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={communities?.title} />

  return (
    <CmschPage title={communities?.titleResort}>
      <Heading as="h1" variant="main-title">
        {communities?.titleResort}
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
      {communities?.descriptionResort && (
        <Box mt={5}>
          <Markdown text={communities?.descriptionResort} />
        </Box>
      )}
      {filteredOrganizations.map((organization) => (
        <CardListItem key={organization.id} data={organization} link={`${AbsolutePaths.ORGANIZATION}/${organization.id}`} />
      ))}
    </CmschPage>
  )
}
