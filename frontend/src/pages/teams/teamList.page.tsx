import { createRef, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { InputGroup } from '@chakra-ui/input'
import { SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputLeftElement } from '@chakra-ui/react'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { TeamView } from '../../util/views/team.view'
import { TeamListItem } from './components/TeamListItem'
import { TeamMock } from './mock'

export default function TeamListPage() {
  const [teams] = useState<TeamView[]>(TeamMock)
  const [filteredTeams, setFilteredTeams] = useState<TeamView[]>(teams)
  const inputRef = createRef<HTMLInputElement>()
  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!search) setFilteredTeams(teams)
    else
      setFilteredTeams(
        teams.filter((c) => {
          return c.name.toLocaleLowerCase().includes(search) || c.id.toLocaleLowerCase().includes(search)
        })
      )
  }
  return (
    <CmschPage>
      <Helmet title="Csapatok" />
      <Heading>Csapatok</Heading>
      <InputGroup mt={5}>
        <InputLeftElement h="100%">
          <SearchIcon />
        </InputLeftElement>
        <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
      </InputGroup>
      {filteredTeams.map((team) => (
        <TeamListItem key={team.id} team={team} />
      ))}
    </CmschPage>
  )
}
