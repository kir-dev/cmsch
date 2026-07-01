import { Table, TableBody, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { useSearch } from '@/util/useSearch'
import type { LeaderBoardItemView } from '@/util/views/leaderBoardView'
import { useCallback, useMemo } from 'react'
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

  const searchFunc = useCallback((item: LeaderBoardItemView, searchWord: string) => {
    return (
      (item.name.toLowerCase().includes(searchWord) ||
        item.groupName?.toLowerCase().includes(searchWord) ||
        item.description?.toLowerCase().includes(searchWord) ||
        item.label?.toLowerCase().includes(searchWord)) ??
      false
    )
  }, [])

  const searchArgs = useSearch(dataWithPosition, searchFunc)

  return (
    <>
      {searchEnabled && (
        <div className="mx-5">
          <SearchBar className="mb-5" {...searchArgs} />
        </div>
      )}
      <Table>
        <TableHeader>
          <TableRow className="sr-only">
            {!categorized && <TableHead>#</TableHead>}
            <TableHead>Név</TableHead>
            {showGroup && <TableHead>Csoport</TableHead>}
            <TableHead>Pont</TableHead>
            <TableHead></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
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
        </TableBody>
      </Table>
      {data.length === 0 && <p className="p-5">Nincs megjeleníthető információ.</p>}
    </>
  )
}
