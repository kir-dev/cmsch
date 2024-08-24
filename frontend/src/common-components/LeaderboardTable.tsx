import { Box, Text } from '@chakra-ui/react'
import { useMemo } from 'react'
import { useSearch } from '../util/useSearch'
import { LeaderBoardItemView } from '../util/views/leaderBoardView'
import { CollapsableTableRow } from './CollapsableTableRow'
import { SearchBar } from './SearchBar'

type LeaderboardTableProps = {
  data: LeaderBoardItemView[]
  showGroup?: boolean
  showDescription?: boolean
  searchEnabled?: boolean
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
  showDescription = false,
  searchEnabled = false
}: LeaderboardTableProps) => {
  const dataWithPosition = useMemo(() => data.map((item, index) => ({ ...item, position: index + 1 })), [data])

  const searchArgs = useSearch(
    dataWithPosition,
    (item, searchWord) =>
      (item.name.toLowerCase().includes(searchWord) ||
        item.groupName?.toLowerCase().includes(searchWord) ||
        item.description?.toLowerCase().includes(searchWord)) ??
      false
  )

  return (
    <>
      {searchEnabled && (
        <Box mx={5}>
          <SearchBar mb={5} {...searchArgs} />
        </Box>
      )}
      <Box>
        {searchArgs.filteredData.map((item, idx) => (
          <CollapsableTableRow
            collapsable={detailed && (item.items || false) && item.items.length > 0}
            key={item.position}
            data={item}
            idx={idx}
            showGroup={showGroup}
            suffix={suffix}
            categorized={categorized}
            showDescription={showDescription}
          />
        ))}
      </Box>
      {data.length === 0 && <Text>Nincs megjeleníthető információ.</Text>}
    </>
  )
}
