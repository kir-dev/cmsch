import { SearchIcon } from '@chakra-ui/icons'
import { InputGroup } from '@chakra-ui/input'
import { Heading, Input, InputLeftElement } from '@chakra-ui/react'
import { createRef, useState } from 'react'
import { Helmet } from 'react-helmet'
import { Paragraph } from '../../common-components/Paragraph'
import { CardListItem } from './components/CardListItem'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Community } from '../../util/views/organization'
import { AbsolutePaths } from '../../util/paths'

const CommunityList = () => {
  // TODO: use backend for content
  const [filteredCommunities, setFilteredCommunities] = useState<Community[]>([])
  const inputRef = createRef<HTMLInputElement>()
  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!search) setFilteredCommunities([])
    else
      setFilteredCommunities(
        ([] as Community[]).filter((c) => {
          if (c.name.toLocaleLowerCase().includes(search)) return true
          if (c.id.toLocaleLowerCase().includes(search)) return true
          for (const keyword of c.searchKeywords || []) {
            if (keyword.toLocaleLowerCase().includes(search)) return true
          }
          for (const interest of c.interests || []) {
            if (interest.toLocaleLowerCase().includes(search)) return true
          }
          return false
        })
      )
  }
  return (
    <CmschPage>
      <Helmet title="Körök" />
      <Heading>Körök</Heading>
      <InputGroup mt={5}>
        <InputLeftElement h="100%">
          <SearchIcon />
        </InputLeftElement>
        <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
      </InputGroup>
      <Paragraph>
        A karon számtalan öntevékeny kör működik, mindenki megtalálhatja az érdeklődési körének megfelelő csoportot. A körök a Schönherz
        Kollégiumban működnek.
      </Paragraph>
      {filteredCommunities.map((community) => (
        <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITIES}/${community.id}`} />
      ))}
    </CmschPage>
  )
}

export default CommunityList
