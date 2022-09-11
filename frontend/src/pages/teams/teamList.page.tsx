import { createRef, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { InputGroup } from '@chakra-ui/input'
import { SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputLeftElement } from '@chakra-ui/react'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { TeamListItemView } from '../../util/views/team.view'
import { TeamListItem } from './components/TeamListItem'
import { useTeamList } from '../../api/hooks/team/useTeamList'
import { Loading } from '../../common-components/Loading'
import { l } from '../../util/language'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'

export default function TeamListPage() {
  const { data: teams, isLoading, isError, error } = useTeamList()
  const [filteredTeams, setFilteredTeams] = useState<TeamListItemView[] | undefined>(teams)
  const { sendMessage } = useServiceContext()
  const inputRef = createRef<HTMLInputElement>()

  if (isLoading) {
    return <Loading />
  }

  if (isError) {
    sendMessage(l('team-list-load-failed') + error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof teams === 'undefined') {
    sendMessage(l('team-list-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!search) setFilteredTeams(teams)
    else
      setFilteredTeams(
        teams?.filter((c) => {
          return c.name.toLocaleLowerCase().includes(search)
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
      {filteredTeams?.map((team) => (
        <TeamListItem key={team.id} team={team} />
      ))}
    </CmschPage>
  )
}
