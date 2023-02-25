import { createRef, useMemo, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { CloseIcon, SearchIcon } from '@chakra-ui/icons'
import { Heading, Input, InputGroup, InputLeftElement, InputRightElement } from '@chakra-ui/react'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { TeamListItem } from './components/TeamListItem'
import { useTeamList } from '../../api/hooks/team/queries/useTeamList'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { PageStatus } from '../../common-components/PageStatus'

export default function TeamListPage() {
  const config = useConfigContext()
  const component = config?.components.team
  const { data, isLoading, isError } = useTeamList()
  const [search, setSearch] = useState('')
  const filteredTeams = useMemo(() => {
    return data?.filter((c) => {
      return c.name.toLocaleLowerCase().includes(search)
    })
  }, [data, search])
  const inputRef = createRef<HTMLInputElement>()

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

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
        {search && (
          <InputRightElement
            h="100%"
            onClick={() => {
              setSearch('')
              if (inputRef.current?.value) inputRef.current.value = ''
            }}
          >
            <CloseIcon />
          </InputRightElement>
        )}
      </InputGroup>
      {filteredTeams?.map((team) => (
        <TeamListItem key={team.id} team={team} detailEnabled={component?.showTeamDetails} />
      ))}
    </CmschPage>
  )
}
