import { Table, TableContainer, Tbody, Text } from '@chakra-ui/react'
import { LeaderBoardItemView } from '../util/views/leaderBoardView'
import { CollapsableTableRow } from './CollapsableTableRow'

type LeaderboardTableProps = {
  data: LeaderBoardItemView[]
  showGroup?: boolean
  suffix?: string
  detailed?: boolean
  categorized?: boolean
}

export const LeaderBoardTable = ({ data, showGroup = false, suffix, detailed = false, categorized = false }: LeaderboardTableProps) => {
  return (
    <>
      <TableContainer>
        <Table variant="striped" colorScheme="brand">
          <Tbody>
            {data.map((item, idx) => (
              <CollapsableTableRow
                collapsable={detailed && (item.items || false) && item.items.length > 0}
                key={item.name}
                data={item}
                idx={idx}
                showGroup={showGroup}
                suffix={suffix}
                categorized={categorized}
              />
            ))}
          </Tbody>
        </Table>
      </TableContainer>
      {data.length === 0 && <Text>Nincs megjeleníthető információ.</Text>}
    </>
  )
}
