import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useLeaderBoardQuery } from '@/api/hooks/leaderboard/useLeaderBoardQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CustomTabButton } from '@/common-components/CustomTabButton'

import { LeaderBoardTable } from '@/common-components/LeaderboardTable'
import { PageStatus } from '@/common-components/PageStatus'
import { Tabs, TabsContent, TabsList } from '@/components/ui/tabs'

const LeaderboardByUserOrGroupPage = () => {
  const component = useConfigContext()?.components?.leaderboard
  const { data, isError, isLoading } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short')

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  const userBoard = component.showUserBoard && (
    <LeaderBoardTable
      searchEnabled={component.searchEnabled}
      data={data?.userBoard || []}
      showGroup={component.showGroupOfUser}
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )
  const groupBoard = component.showGroupBoard && (
    <LeaderBoardTable
      searchEnabled={component.searchEnabled}
      data={data?.groupBoard || []}
      detailed={component.leaderboardDetailsEnabled}
      suffix="pont"
    />
  )

  return (
    <>
      {component.showUserBoard && component.showGroupBoard ? (
        <Tabs defaultValue="user" className="w-full">
          <TabsList className="mb-5 px-8 flex justify-start">
            {data?.userBoard && <CustomTabButton value="user">Egyéni</CustomTabButton>}
            {data?.groupBoard && <CustomTabButton value="group">Csoportos</CustomTabButton>}
          </TabsList>
          {data?.userBoard && (
            <TabsContent value="user" className="px-0">
              {userBoard}
            </TabsContent>
          )}
          {data?.groupBoard && (
            <TabsContent value="group" className="px-0">
              {groupBoard}
            </TabsContent>
          )}
        </Tabs>
      ) : (
        <>
          {data?.userBoard && userBoard}
          {data?.groupBoard && groupBoard}
        </>
      )}
    </>
  )
}

export default LeaderboardByUserOrGroupPage
