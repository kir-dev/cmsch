import { Heading } from '@chakra-ui/react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useTinderCommunity } from '../../api/hooks/community/useTinderCommunity.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { AbsolutePaths } from '../../util/paths.ts'
import { TinderStatus } from '../../util/views/tinder.ts'
import { CardListItem } from './components/CardListItem.tsx'

export default function LikedCommunityListPage() {
  const config = useConfigContext()?.components
  const app = config?.app
  const communities = config?.communities
  const { data, isLoading, isError } = useTinderCommunity()

  if (!communities || !communities.tinderEnabled) return <ComponentUnavailable />
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  const likedCommunities = data.filter((c) => c.status === TinderStatus.LIKED)

  return (
    <CmschPage loginRequired>
      <title>{app?.siteName || 'CMSch'} | Kedvelt közösségek</title>
      <Heading as="h1" variant="main-title">
        Kedvelt közösségek
      </Heading>
      {likedCommunities?.map((community) => (
        <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITY}/${community.id}`} />
      ))}
    </CmschPage>
  )
}
