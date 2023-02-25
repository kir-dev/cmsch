import { SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputGroup, InputLeftElement } from '@chakra-ui/react'
import { createRef, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Paragraph } from '../../common-components/Paragraph'
import { CardListItem } from './components/CardListItem'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Organization } from '../../util/views/organization'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'
import { useOrganizationList } from '../../api/hooks/community/useOrganizationList'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { PageStatus } from '../../common-components/PageStatus'

export default function OrganizationListPage() {
  const config = useConfigContext()?.components.communities
  const { data, isLoading, isError } = useOrganizationList()
  const [filteredOrganizations, setFilteredOrganizations] = useState<Organization[]>(data || [])
  const inputRef = createRef<HTMLInputElement>()

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={config?.title} />

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!search) setFilteredOrganizations(data)
    else setFilteredOrganizations(data.filter((c) => c.name.toLocaleLowerCase().includes(search)))
  }

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
      <Paragraph>{l('organization-description')}</Paragraph>
      {filteredOrganizations.map((organization) => (
        <CardListItem key={organization.id} data={organization} link={`${AbsolutePaths.ORGANIZATION}/${organization.id}`} />
      ))}
    </CmschPage>
  )
}
