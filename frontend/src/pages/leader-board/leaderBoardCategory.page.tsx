import { SearchIcon } from '@chakra-ui/icons'
import {
  Flex,
  Heading,
  Input,
  InputGroup,
  InputLeftElement,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  Text,
  useBreakpoint,
  VStack
} from '@chakra-ui/react'
import { createRef, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CustomTabButton } from '../../common-components/CustomTabButton'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { LinkButton } from '../../common-components/LinkButton'
import { PageStatus } from '../../common-components/PageStatus'
import { AbsolutePaths } from '../../util/paths'
import { LeaderBoardView } from '../../util/views/leaderBoardView'

const mockData: LeaderBoardView = {
  userScore: 11,
  userBoard: [
    { id: 0, name: 'Anyabaszo', items: [{ name: 'Feladatok', value: 84 }], total: 84, groupName: 'anyad' },
    { id: 2, name: 'Test', items: [{ name: 'rip', value: 11 }], total: 11, groupName: 'nop' }
  ],
  groupScore: 35,
  groupBoard: [
    { id: 0, name: 'group1', items: [{ name: 'asdasd', value: 84 }], total: 84, groupName: 'anyad' },
    { id: 2, name: 'group2', items: [{ name: 'test23', value: 11 }], total: 11, groupName: 'nop' }
  ]
}
export default function LeaderboardCategoryPage() {
  //const { data, isLoading, isError } = useLeaderBoardQuery('categorized')
  const component = useConfigContext()?.components.leaderboard
  const breakpoint = useBreakpoint()
  const inputRef = createRef<HTMLInputElement>()
  const isError = false
  const isLoading = false
  const data = mockData
  const [filteredData, setFilteredData] = useState<LeaderBoardView | undefined>(data)

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  if (!component.leaderboardDetailsByCategoryEnabled) return <Navigate to={AbsolutePaths.LEADER_BOARD} />

  const handleInput = () => {
    const search = inputRef?.current?.value.toLowerCase()
    if (!data) {
      setFilteredData(undefined)
    } else if (!search) setFilteredData(data)
    else {
      setFilteredData({
        userBoard: data.userBoard?.filter((item) => {
          return item.name.toLocaleLowerCase().includes(search)
        }),
        groupBoard: data.groupBoard?.filter((item) => {
          return item.name.toLocaleLowerCase().includes(search)
        }),
        userScore: data.userScore
      })
    }
  }
  const userBoard = component.showUserBoard && (
    <LeaderBoardTable
      data={filteredData?.userBoard || []}
      showGroup={component.showGroupOfUser}
      categorized
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = component.showGroupBoard && (
    <LeaderBoardTable data={filteredData?.groupBoard || []} detailed={component.leaderboardDetailsEnabled} suffix="pont" categorized />
  )
  return (
    <CmschPage>
      <Helmet title={title} />
      <Flex wrap="wrap" justify="space-between">
        <VStack mb={5} align="flex-start">
          <Heading>{title}</Heading>
          <Text>Kategóriák szerint</Text>
        </VStack>
        <VStack>
          <LinkButton href={AbsolutePaths.LEADER_BOARD} my={5}>
            Összesített nézet
          </LinkButton>
        </VStack>
      </Flex>
      {component.searchEnabled && (
        <InputGroup mt={5}>
          <InputLeftElement h="100%">
            <SearchIcon />
          </InputLeftElement>
          <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
        </InputGroup>
      )}

      {component.showUserBoard && component.showGroupBoard ? (
        <Tabs size={{ base: 'sm', md: 'md' }} isFitted={breakpoint !== 'base'} variant="soft-rounded" colorScheme="brand">
          <TabList>
            {data?.userBoard && <CustomTabButton>Egyéni</CustomTabButton>}
            {data?.groupBoard && <CustomTabButton>Csoportos</CustomTabButton>}
          </TabList>
          <TabPanels>
            {data?.userBoard && <TabPanel px={0}>{userBoard}</TabPanel>}

            {data?.groupBoard && <TabPanel px={0}>{groupBoard}</TabPanel>}
          </TabPanels>
        </Tabs>
      ) : (
        <>
          {data?.userBoard && userBoard}
          {data?.groupBoard && groupBoard}
        </>
      )}
    </CmschPage>
  )
}
