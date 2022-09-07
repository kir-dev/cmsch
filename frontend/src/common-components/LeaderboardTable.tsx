import { TableContainer, Table, Tbody, Tr, Td, Text } from '@chakra-ui/react'
import { LeaderBoardItemView } from '../util/views/leaderBoardView'

type LeaderboardTableProps = {
  data: LeaderBoardItemView[]
  showGroup?: boolean
  suffix?: string
}

export const LeaderBoardTable = ({ data, showGroup = false, suffix }: LeaderboardTableProps) => {
  return (
    <>
      <TableContainer>
        <Table variant="striped" colorScheme="brand">
          <Tbody>
            {data.map((item, idx) => (
              <Tr key={item.name}>
                <Td>{idx + 1}.</Td>
                <Td>{item.name}</Td>
                {showGroup && <Td>{item.groupName}</Td>}
                <Td>{`${item.score} ${suffix || ''}`}</Td>
              </Tr>
            ))}
          </Tbody>
        </Table>
      </TableContainer>
      {data.length === 0 && <Text>Nincs megjeleníthető információ.</Text>}
    </>
  )
}
