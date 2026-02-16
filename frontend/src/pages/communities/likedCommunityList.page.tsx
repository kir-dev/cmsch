import { Box, Button, Flex, Heading, Text } from '@chakra-ui/react'
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
      <Box w="100%" mx="auto" px={{ base: 2, md: 4 }}>
        <Box
          position="relative"
          mb={6}
          display="flex"
          flexDirection={{ base: 'column', sm: 'row' }}
          alignItems={{ base: 'center', md: 'flex-start' }}
          gap={4}
        >
          <Heading as="h1" variant="main-title" textAlign={{ base: 'center', sm: 'left' }} flex={{ base: 'none', sm: 1 }}>
            Kör tinder
          </Heading>
          <Flex
            flexDirection={{ base: 'column', md: 'row' }}
            gap={3}
            width={{ base: 'full', sm: 'auto' }}
            position={{ base: 'relative', sm: 'absolute' }}
            top={{ base: 'auto', sm: '50%' }}
            right={{ base: 'auto', sm: 2 }}
            transform={{ base: 'none', sm: 'translateY(-30%)', md: 'translateY(-50%)' }}
          >
            <Button
              as={Link}
              to={`${AbsolutePaths.TINDER}/community`}
              size={{ base: 'md', md: 'lg' }}
              aria-label="Tinder-matches-button"
              width={{ base: 'full', sm: 'auto' }}
            >
              Tinder
            </Button>
            <Button as={Link} to={`${AbsolutePaths.COMMUNITY}`} size={{ base: 'md', md: 'lg' }} width={{ base: 'full', sm: 'auto' }}>
              Összes kör megtekintése
            </Button>
          </Flex>
        </Box>
      </Box>
      {likedCommunities.length === 0 && (
        <Box px={{ base: 4, md: 0 }} py={8} textAlign="center">
          <Text>Nem kedveltél még egy kört sem. A fenti Tinder gombra kattintva kereshetsz a még nem látott körök közt.</Text>
        </Box>
      )}
      <Box px={{ base: 2, md: 0 }}>
        {likedCommunities?.map((community) => (
          <CardListItem key={community.id} data={community} link={`${AbsolutePaths.COMMUNITY}/${community.id}`} />
        ))}
      </Box>
    </CmschPage>
  )
}
