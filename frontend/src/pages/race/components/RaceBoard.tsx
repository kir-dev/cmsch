import type { Race } from '@/api/contexts/config/types'
import { BoardStat } from '@/common-components/BoardStat'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LeaderBoardTable } from '@/common-components/LeaderboardTable'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Separator } from '@/components/ui/separator'
import type { RaceView } from '@/util/views/race.view'
import { useMemo } from 'react'

type Props = {
  data: RaceView | undefined
  component?: Race
  isError: boolean
  isLoading: boolean
}

const RaceBoard = ({ data, component, isError, isLoading }: Props) => {
  const showDescription = useMemo(() => data?.board.some((i) => !!i.description), [data?.board])

  if (!component || !component.visible) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage title={data.categoryName}>
      <h1 className="text-3xl font-bold font-heading mb-3">{data.categoryName}</h1>
      <Markdown text={data.description || component.defaultCategoryDescription} />
      <div className="flex flex-wrap my-5 gap-5">
        <BoardStat label="Helyezésed" value={data.place || '-'} />
        <BoardStat label="Legjobb időd" value={(data.bestTime || '-') + ' mp'} />
      </div>
      <Separator className="mb-10" />
      <LeaderBoardTable
        searchEnabled={component.searchEnabled}
        data={data.board || []}
        showGroup={true}
        suffix="mp"
        showDescription={showDescription}
      />
    </CmschPage>
  )
}
export default RaceBoard
