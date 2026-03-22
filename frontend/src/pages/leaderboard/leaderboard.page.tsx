import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useLeaderBoardQuery } from '@/api/hooks/leaderboard/useLeaderBoardQuery'
import { BoardStat } from '@/common-components/BoardStat'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown.tsx'
import { PageStatus } from '@/common-components/PageStatus'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { useMatch, useNavigate } from 'react-router'
import LeaderboardByCategoryPage from './leaderboardByCategory.page.tsx'
import LeaderboardByUserOrGroupPage from './leaderboardByUserOrGroup.page.tsx'

const LeaderboardPage = () => {
  const component = useConfigContext()?.components?.leaderboard
  const { data, isError, isLoading } = useLeaderBoardQuery(component?.leaderboardDetailsEnabled ? 'detailed' : 'short')
  const byCategory = useMatch('/leaderboard/category')
  const navigate = useNavigate()

  if (!component) return <ComponentUnavailable />

  const title = component.title || 'Toplista'

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={title} />

  const tabValue = byCategory ? 'category' : 'main'

  const onTabSelected = (val: string) => {
    switch (val) {
      case 'main':
        navigate('/leaderboard')
        break
      case 'category':
        navigate('/leaderboard/category')
        break
    }
  }

  return (
    <CmschPage disablePadding={true} title={title}>
      <h1 className="m-8 text-center text-4xl font-bold tracking-tight">{title}</h1>
      {component.topMessage && (
        <div className="text-center">
          <Markdown text={component.topMessage} />
        </div>
      )}
      <div className="my-5 flex gap-4">
        {data?.userScore !== undefined && <BoardStat className="flex-1" label="Saját pont" value={data.userScore} />}
        {data?.groupScore !== undefined && (
          <BoardStat className="flex-1" label={`${component.myGroupName} pontjai`} value={data.groupScore} />
        )}
      </div>

      <Tabs defaultValue={tabValue} onValueChange={onTabSelected} className="w-full">
        <TabsList className="flex w-full">
          <TabsTrigger value="main" className="flex-1">
            {component.groupBoardName}
          </TabsTrigger>
          {component.leaderboardDetailsByCategoryEnabled && (
            <TabsTrigger value="category" className="flex-1">
              {component.leaderBoardCategoryName}
            </TabsTrigger>
          )}
        </TabsList>
        <TabsContent value="main" className="px-0">
          <LeaderboardByUserOrGroupPage />
        </TabsContent>
        <TabsContent value="category" className="px-0">
          <LeaderboardByCategoryPage />
        </TabsContent>
      </Tabs>
    </CmschPage>
  )
}

export default LeaderboardPage
