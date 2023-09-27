import { Table, TableContainer, Tbody, Text } from '@chakra-ui/react'
import { LeaderBoardItemView } from '../util/views/leaderBoardView'
import { CollapsableTableRow } from './CollapsableTableRow'

type LeaderboardTableProps = {
  data: LeaderBoardItemView[]
  showGroup?: boolean
  showDescription?: boolean
  suffix?: string
  detailed?: boolean
  categorized?: boolean
}

export const LeaderBoardTable = ({
  data,
  showGroup = false,
  suffix,
  detailed = false,
  categorized = false,
  showDescription = false
}: LeaderboardTableProps) => {
  const dataWithPosition = data.map((item, idx) => ({ ...item, position: idx + 1 }))
  return (
    <>
      <TableContainer>
        <Table variant="unstyled">
          <Tbody>
            {dataWithPosition.map((item, idx) => (
              <CollapsableTableRow
                collapsable={detailed && (item.items || false) && item.items.length > 0}
                key={item.name}
                data={item}
                idx={idx}
                showGroup={showGroup}
                suffix={suffix}
                categorized={categorized}
                showDescription={showDescription}
              />
            ))}
          </Tbody>
        </Table>
      </TableContainer>
      {data.length === 0 && <Text>Nincs megjeleníthető információ.</Text>}
    </>
  )
}
