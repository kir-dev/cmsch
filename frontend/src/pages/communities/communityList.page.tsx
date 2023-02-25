import { SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputGroup, InputLeftElement } from '@chakra-ui/react'
import { createRef, useState } from 'react'
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
  console.log(isError, isLoading)
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={config?.title} />

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!data) {
      setFilteredCommunities([])
    } else if (!search) setFilteredCommunities(data)
    else setFilteredCommunities(data?.filter((c) => c.name.toLocaleLowerCase().includes(search)) || [])
  }

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
