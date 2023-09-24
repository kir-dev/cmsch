import {
  Flex,
  Heading,
  HStack,
  Input,
  InputGroup,
  InputLeftElement,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  useBreakpoint,
  useBreakpointValue,
  VStack
} from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { useLeaderBoardQuery } from '../../api/hooks/leaderboard/useLeaderBoardQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { BoardStat } from '../../common-components/BoardStat'
import { CustomTabButton } from '../../common-components/CustomTabButton'
import { LeaderBoardTable } from '../../common-components/LeaderboardTable'
import { AbsolutePaths } from '../../util/paths'
import { LinkButton } from '../../common-components/LinkButton'
import { PageStatus } from '../../common-components/PageStatus'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { SearchIcon } from '@chakra-ui/icons'
import { createRef, useState } from 'react'
import { LeaderBoardView } from '../../util/views/leaderBoardView'
const LeaderboardPage = () => {
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  const component = useConfigContext()?.components.leaderboard
  const { data, isError, isLoading } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short')

  const [filteredData, setFilteredData] = useState<LeaderBoardView | undefined>(data)
  const inputRef = createRef<HTMLInputElement>()
  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  const userBoard = component.showUserBoard && (
    <LeaderBoardTable
      data={filteredData?.userBoard || []}
      showGroup={component.showGroupOfUser}
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = component.showGroupBoard && (
    <LeaderBoardTable data={filteredData?.groupBoard || []} detailed={component.leaderboardDetailsEnabled} suffix="pont" />
  )
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

  return (
    <CmschPage>
      <Helmet title={title} />
      <Flex wrap="wrap" justify="space-between">
        <VStack>
          <Heading>{title}</Heading>
        </VStack>
        {component.leaderboardDetailsByCategoryEnabled && (
          <VStack>
            <LinkButton href={AbsolutePaths.LEADER_BOARD + '/category'} my={5}>
              Kategóriák nézet
            </LinkButton>
          </VStack>
        )}
      </Flex>
      {component.searchEnabled && (
        <InputGroup mt={5}>
          <InputLeftElement h="100%">
            <SearchIcon />
          </InputLeftElement>
          <Input ref={inputRef} placeholder="Keresés..." size="lg" onChange={handleInput} autoFocus={true} />
        </InputGroup>
      )}

      <HStack my={5}>
        {data?.userScore !== undefined && <BoardStat label="Saját pont" value={data.userScore} />}
        {data?.groupScore !== undefined && <BoardStat label="Csapat pont" value={data.groupScore} />}
      </HStack>

      {component.showUserBoard && component.showGroupBoard ? (
        <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="soft-rounded" colorScheme="brand">
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

export default LeaderboardPage
