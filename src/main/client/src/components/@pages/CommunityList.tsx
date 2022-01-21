import { Heading, Input } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React, { createRef, useState } from 'react'
import { Paragraph } from '../@commons/Basics'
import { COMMUNITIES } from '../../content/communities'
import { CardListItem } from '../@commons/CardListItem'
import { Community } from '../../types/Organization'
import { Helmet } from 'react-helmet'

type CommunityListProps = {}

export const CommunityList: React.FC<CommunityListProps> = () => {
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
      <Input ref={inputRef} placeholder="Keresés..." size="lg" boxShadow="md" marginTop={5} onChange={handleInput} />
      <Paragraph>
        A karon számtalan öntevékeny kör működik, mindenki megtalálhatja az érdeklődési körének megfelelő csoportot. A körök többsége a
        Schönherz Kollégiumban működik, de számtalan lehetőség található a Nagytétényi úti kollégiumban is.
      </Paragraph>
      {filteredCommunities.map((community) => {
        return <CardListItem data={community} link={'/korok/' + community.id} />
      })}
    </Page>
  )
}
