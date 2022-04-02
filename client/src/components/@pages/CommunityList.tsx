import { SearchIcon } from '@chakra-ui/icons'
import { InputGroup } from '@chakra-ui/input'
import { Heading, Input, InputLeftElement } from '@chakra-ui/react'
import { createRef, FC, useState } from 'react'
import { Helmet } from 'react-helmet'
import { COMMUNITIES } from '../../content/communities'
import { Community } from '../../types/Organization'
import { CardListItem } from '../@commons/CardListItem'
import { Paragraph } from '../@commons/Paragraph'
import { Page } from '../@layout/Page'

type CommunityListProps = {}

export const CommunityList: FC<CommunityListProps> = () => {
  const [filteredCommunities, setFilteredCommunities] = useState<Community[]>(COMMUNITIES)
  const inputRef = createRef<HTMLInputElement>()
  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!search) setFilteredCommunities(COMMUNITIES)
    else
      setFilteredCommunities(
        COMMUNITIES.filter((c) => {
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
    <Page>
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
        <CardListItem key={community.id} data={community} link={'/korok/' + community.id} />
      ))}
    </Page>
  )
}
