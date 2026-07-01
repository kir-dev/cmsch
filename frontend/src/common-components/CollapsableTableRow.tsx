import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import { TableCell, TableRow } from '@/components/ui/table'
import { cn } from '@/lib/utils'
import TeamLabel from '@/pages/teams/components/TeamLabel.tsx'
import { joinPath } from '@/util/core-functions.util'
import { AbsolutePaths } from '@/util/paths'
import type { LeaderBoardItemView } from '@/util/views/leaderBoardView'
import { ChevronDown, ChevronUp } from 'lucide-react'
import { Fragment, useState } from 'react'
import { Link } from 'react-router'
import { TokenRarityDisplay } from './TokenRarityDisplay.tsx'

type CollapsableTableRowProps = {
  collapsable: boolean
  data: LeaderBoardItemView & { position: number }
  idx: number
  showGroup: boolean
  suffix?: string
  categorized?: boolean
  showDescription: boolean
}

export const CollapsableTableRow = ({
  collapsable,
  data,
  idx,
  suffix,
  showGroup,
  categorized = false,
  showDescription
}: CollapsableTableRowProps) => {
  const [isOpen, setIsOpen] = useState(false)
  const isGroupLink = typeof data.groupId !== 'undefined'
  const actualCollapsable = (collapsable && data.items && data.items.length > 0) || false
  const bgClass = idx % 2 === 0 ? 'bg-secondary/50' : ''

  const colSpan = (categorized ? 0 : 1) + 1 + (showGroup ? 1 : 1) + 1

  return (
    <Collapsible open={isOpen} onOpenChange={setIsOpen} asChild>
      <>
        <CollapsibleTrigger asChild disabled={!actualCollapsable}>
          <TableRow
            onClick={() => {
              if (actualCollapsable) setIsOpen(!isOpen)
            }}
            className={cn('font-bold transition-colors', bgClass, actualCollapsable ? 'cursor-pointer' : 'cursor-default')}
          >
            {!categorized && <TableCell className="w-[40px]">{data.position}.</TableCell>}
            <TableCell>
              <div className="flex flex-col md:flex-row md:items-center gap-1">
                <span>{data.name}</span>
                {data.label && <TeamLabel name={data.label} color={data.labelColor} />}
              </div>
            </TableCell>
            {showGroup && data.groupName && (
              <TableCell>
                {isGroupLink ? (
                  <Link to={joinPath(AbsolutePaths.TEAMS, 'details', data.groupId)} className="underline">
                    {data.groupName}
                  </Link>
                ) : (
                  data.groupName
                )}
              </TableCell>
            )}
            <TableCell className="text-right whitespace-nowrap">
              {data.score || data.total ? `${new Intl.NumberFormat('hu-HU').format(data.score || data.total || 0)} ${suffix || ''}` : ''}
            </TableCell>
            <TableCell className="w-[28px]">
              {actualCollapsable && (isOpen ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />)}
            </TableCell>
          </TableRow>
        </CollapsibleTrigger>

        {showDescription && data.description && (
          <TableRow className={bgClass}>
            <TableCell colSpan={colSpan} className="pb-1 pt-0">
              {data.description}
            </TableCell>
          </TableRow>
        )}

        <CollapsibleContent asChild>
          <>
            {data.items
              ?.sort((a, b) => b.value - a.value)
              .map((item, itemIndex) => (
                <Fragment key={item.name}>
                  <TableRow className={cn(bgClass, 'font-normal')}>
                    {categorized && <TableCell className="w-[40px]">{itemIndex + 1}.</TableCell>}
                    <TableCell colSpan={categorized ? 1 : 2} className="pl-8">
                      {item.name}
                    </TableCell>
                    <TableCell className="text-right whitespace-nowrap">
                      {`${new Intl.NumberFormat('hu-HU').format(item.value)} ${suffix || ''}`}
                    </TableCell>
                    {!categorized && <TableCell className="w-[28px]" />}
                  </TableRow>
                  {item.name === 'QR kódok' && data.tokenRarities && (
                    <TableRow className={bgClass}>
                      <TableCell colSpan={colSpan}>
                        <TokenRarityDisplay collected={data.tokenRarities} />
                      </TableCell>
                    </TableRow>
                  )}
                </Fragment>
              ))}
          </>
        </CollapsibleContent>
      </>
    </Collapsible>
  )
}
