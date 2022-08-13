import {
  Divider,
  HStack,
  Table,
  TableContainer,
  TabList,
  TabPanel,
  TabPanels,
  Tabs,
  Tbody,
  Td,
  Text,
  Tr,
  useBreakpoint,
  useBreakpointValue
} from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'

import { CmschPage } from '../../common-components/layout/CmschPage'
import { useLeaderBoardQuery } from '../../api/hooks/useLeaderBoardQuery'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { BoardStat } from './components/BoardStat'
import { CustomTab } from '../events/components/CustomTab'

const LeaderboardPage = () => {
  const { data } = useLeaderBoardQuery(() => console.log('Leaderboard query failed!'))

  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()

  const leadboardConfig = useConfigContext()?.components.leaderboard
  const title = leadboardConfig?.title || 'Toplista'

  return (
    <CmschPage>
      <Helmet title={title} />
      <HStack my={5}>
        {data?.userScore !== undefined && <BoardStat label="Saját pont" value={data.userScore} />}
        {data?.groupScore !== undefined && <BoardStat label="Csapat pont" value={data.groupScore} />}
      </HStack>
      <Divider mb={10} />

      <Tabs size={tabsSize} isFitted={breakpoint !== 'base'} variant="unstyled">
        <TabList>
          {data?.userBoard && <CustomTab>Egyéni</CustomTab>}
          {data?.groupBoard && <CustomTab>Csoportos</CustomTab>}
        </TabList>
        <TabPanels>
          {data?.userBoard && (
            <TabPanel>
              <TableContainer>
                <Table variant="striped" colorScheme="brand">
                  <Tbody>
                    {data.userBoard.map((item) => (
                      <Tr key={item.name}>
                        <Td>{item.name}</Td>
                        <Td>{item.score}</Td>
                      </Tr>
                    ))}
                  </Tbody>
                </Table>
              </TableContainer>
              {data.userBoard.length === 0 && <Text>Nincs megjeleníthető információ.</Text>}
            </TabPanel>
          )}

          {data?.groupBoard && (
            <TabPanel>
              <TableContainer>
                <Table variant="striped" colorScheme="brand">
                  <Tbody>
                    {data.groupBoard.map((item) => (
                      <Tr key={item.name}>
                        <Td>{item.name}</Td>
                        <Td>{item.score}</Td>
                      </Tr>
                    ))}
                  </Tbody>
                </Table>
              </TableContainer>
              {data.groupBoard.length === 0 && <Text>Nincs megjeleníthető információ.</Text>}
            </TabPanel>
          )}
        </TabPanels>
      </Tabs>
    </CmschPage>
  )
}

export default LeaderboardPage
