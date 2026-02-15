import { Box, Button, Flex, Heading } from '@chakra-ui/react'
import { Link } from 'react-router'
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
      <Box position="relative" mb={6}>
        <Heading as="h1" variant="main-title">
          Kör tinder
        </Heading>
        <Flex position="absolute" top="50%" right={2} transform="translateY(-50%)" gap={3}>
          <Button as={Link} to={`${AbsolutePaths.TINDER}/community`} size="lg" aria-label="Tinder-matches-button">
            Tinder
          </Button>
          <Button as={Link} to={`${AbsolutePaths.COMMUNITY}`} size="lg">
            Összes kör megtekintése
          </Button>
        </Flex>
      </Box>
      {likedCommunities.length === 0 && (
        <div>Nem kedveltél még egy kört sem. A fenti Tinder gombra kattintva kereshetsz a még nem látott körök közt.</div>
      )}
      {likedCommunities?.map((community) => (
        <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITY}/${community.id}`} />
      ))}
    </CmschPage>
  )
}
