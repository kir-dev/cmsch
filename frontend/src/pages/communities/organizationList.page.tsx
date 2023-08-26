import { SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputGroup, InputLeftElement, Text } from '@chakra-ui/react'
import { createRef, useEffect, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useOrganizationList } from '../../api/hooks/community/useOrganizationList'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { Organization } from '../../util/views/organization'
import { CardListItem } from './components/CardListItem'

export default function OrganizationListPage() {
  const config = useConfigContext()?.components.communities
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
      setFilteredOrganizations(data)
      if (inputRef.current) inputRef.current.value = ''
    }
  }, [data])

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={config?.title} />

  return (
    <CmschPage>
      <Helmet title={l('organization-title')} />
      <Heading>{l('organization-title')}</Heading>
      <InputGroup mt={5}>
        <InputLeftElement h="100%">
          <SearchIcon />
        </InputLeftElement>
        <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
      </InputGroup>
      <Text mt={5}>{l('organization-description')}</Text>
      {filteredOrganizations.map((organization) => (
        <CardListItem key={organization.id} data={organization} link={`${AbsolutePaths.ORGANIZATION}/${organization.id}`} />
      ))}
    </CmschPage>
  )
}
