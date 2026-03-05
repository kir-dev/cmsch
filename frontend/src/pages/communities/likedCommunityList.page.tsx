import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useTinderCommunity } from '@/api/hooks/community/useTinderCommunity.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage.tsx'
import { PageStatus } from '@/common-components/PageStatus.tsx'
import { Button } from '@/components/ui/button'
import { AbsolutePaths } from '@/util/paths.ts'
import { TinderStatus } from '@/util/views/tinder.ts'
import { Link } from 'react-router'
import { CardListItem } from './components/CardListItem.tsx'

export default function LikedCommunityListPage() {
  const communities = useConfigContext()?.components?.communities
  const { data, isLoading, isError } = useTinderCommunity()

  if (!communities || !communities.tinderEnabled) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  const likedCommunities = data.filter((c) => c.status === TinderStatus.LIKED)

  return (
    <CmschPage loginRequired={true} title="Kedvelt közösségek">
      <div className="w-full mx-auto px-2 md:px-4">
        <div className="relative mb-6 flex flex-col sm:flex-row items-center md:items-start gap-4">
          <h1 className="text-3xl font-bold font-heading text-center sm:text-left flex-none sm:flex-1">Kör tinder</h1>
          <div
            className={
              'flex flex-col md:flex-row gap-3 w-full sm:w-auto sm:absolute ' +
              'sm:top-1/2 sm:right-2 sm:-translate-y-[30%] md:-translate-y-1/2'
            }
          >
            <Button asChild size="lg" variant="outline" className="w-full sm:w-auto">
              <Link to={`${AbsolutePaths.TINDER}/community`}>Tinder</Link>
            </Button>
            <Button asChild size="lg" className="w-full sm:w-auto">
              <Link to={`${AbsolutePaths.COMMUNITY}`}>Összes kör megtekintése</Link>
            </Button>
          </div>
        </div>
      </div>
      {likedCommunities.length === 0 && (
        <div className="px-4 md:px-0 py-8 text-center">
          <p>Nem kedveltél még egy kört sem. A fenti Tinder gombra kattintva kereshetsz a még nem látott körök közt.</p>
        </div>
      )}
      <div className="px-2 md:px-0">
        {likedCommunities?.map((community) => (
          <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITY}/${community.id}`} />
        ))}
      </div>
    </CmschPage>
  )
}
