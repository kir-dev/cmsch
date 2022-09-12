import { createRef, useMemo, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { InputGroup } from '@chakra-ui/input'
import { SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputLeftElement } from '@chakra-ui/react'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { TeamListItem } from './components/TeamListItem'
import { useTeamList } from '../../api/hooks/team/useTeamList'
import { Loading } from '../../common-components/Loading'
import { l } from '../../util/language'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

export default function TeamListPage() {
  const config = useConfigContext()
  const component = config?.components.team
  const { data: teams, isLoading, isError, error } = useTeamList()
  const [search, setSearch] = useState('')
  const filteredTeams = useMemo(() => {
    return teams?.filter((c) => {
      return c.name.toLocaleLowerCase().includes(search)
    })
  }, [teams, search])
  const { sendMessage } = useServiceContext()
  const inputRef = createRef<HTMLInputElement>()

  if (isLoading) {
    return <Loading />
  }

  if (isError) {
    sendMessage(l('team-list-load-failed') + error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof teams === 'undefined' || !component) {
    sendMessage(l('team-list-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  const handleInput = () => {
    const searchFieldValue = inputRef?.current?.value.toLowerCase()
    if (!searchFieldValue) setSearch('')
    else setSearch(searchFieldValue)
  }

  return (
    <CmschPage>
      <Helmet title={component.title} />
      <Heading>{component.title}</Heading>
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
