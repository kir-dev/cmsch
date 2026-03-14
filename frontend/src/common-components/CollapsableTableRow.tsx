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

  const outerColTemplate: string[] = []
  if (!categorized) outerColTemplate.push('auto')
  outerColTemplate.push('1fr')
  if (showGroup) outerColTemplate.push('1fr')
  outerColTemplate.push('auto 20px')

  const innerColTemplate: string[] = []
  if (categorized) innerColTemplate.push('auto')
  innerColTemplate.push('1fr auto')
  if (!categorized) innerColTemplate.push('20px')

  return (
    <>
      <div
        onClick={() => {
          if (actualCollapsable) setIsOpen(!isOpen)
        }}
        className={cn(
          'grid gap-3 p-3 font-bold transition-colors',
          idx % 2 === 0 ? 'bg-secondary/50' : '',
          actualCollapsable ? 'cursor-pointer' : 'cursor-default'
        )}
        style={{ gridTemplateColumns: outerColTemplate.join(' ') }}
      >
        {!categorized && <div>{data.position}.</div>}
        <div>
          <div className="flex flex-col md:flex-row md:items-center">
            <div>{data.name}</div> {data.label && <TeamLabel name={data.label} color={data.labelColor} />}
          </div>
        </div>
        {showGroup && data.groupName && (
          <div className="col-start-group">
            {isGroupLink ? (
              <Link to={joinPath(AbsolutePaths.TEAMS, 'details', data.groupId)} className="underline">
                {data.groupName}
              </Link>
            ) : (
              data.groupName
            )}
          </div>
        )}
        {(data.score || data.total) && (
          <div className="justify-self-end">
            {`${new Intl.NumberFormat('hu-HU').format(data.score || data.total || 0)} ${suffix || ''}`}
          </div>
        )}
        {actualCollapsable && (
          <div className="flex items-center justify-center">
            {isOpen ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
          </div>
        )}
      </div>

      {showDescription && data.description && (
        <div className={cn('pb-1 pt-0', idx % 2 === 0 ? 'bg-secondary/50' : '')}>{data.description}</div>
      )}

      {isOpen && (
        <div
          className={cn('grid gap-x-3 gap-y-1 p-3 pt-0', idx % 2 === 0 ? 'bg-secondary/50' : '')}
          style={{ gridTemplateColumns: innerColTemplate.join(' ') }}
        >
          {data.items
            ?.sort((a, b) => b.value - a.value)
            .map((item, itemIndex) => (
              <Fragment key={item.name}>
                {categorized && <div className="col-start-place">{itemIndex + 1}.</div>}
                <div className="col-start-name">{item.name}</div>
                <div className="justify-self-end">{`${new Intl.NumberFormat('hu-HU').format(item.value)} ${suffix || ''}`}</div>

                {item.name === 'QR kódok' && data.tokenRarities && (
                  <div className="col-span-full">
                    <TokenRarityDisplay collected={data.tokenRarities} />
                  </div>
                )}
              </Fragment>
            ))}
        </div>
      )}
    </>
  )
}
