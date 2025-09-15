import {
  Alert,
  AlertIcon,
  Box,
  Button,
  Center,
  CircularProgress,
  CircularProgressLabel,
  Divider,
  Flex,
  Grid,
  Heading,
  Link,
  Text,
  useColorModeValue,
  VStack
} from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate } from 'react-router'

import React, { useEffect } from 'react'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useProfileQuery } from '../../api/hooks/profile/useProfileQuery.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { PresenceAlert } from '../../common-components/PresenceAlert'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths'
import templateStringReplace from '../../util/templateStringReplace'
import { GuildType, RoleType } from '../../util/views/profile.view'
import { GroupLeaderContactList } from './components/GroupLeaderContactList'
import { ProfileQR } from './components/ProfileQR'
import { completedPercent, submittedPercent } from './util/percentFunctions'

const Map = React.lazy(() => import('../../common-components/map/GroupMapContainer'))

const ProfilePage = () => {
  const { onLogout } = useAuthContext()
  const { isLoading: profileLoading, data: profile, error: profileError, refetch } = useProfileQuery()
  const navigate = useNavigate()

  useEffect(() => {
    const savedPath = sessionStorage.getItem('path') // Only navigate back to path if it happened in the current session
    if (savedPath) {
      sessionStorage.removeItem('path')
      navigate(savedPath)
    }
    refetch()
  }, [navigate, refetch])

  const config = useConfigContext()?.components
  const component = config?.profile

  const brandColor = useBrandColor()
  const greenProgressColor = useColorModeValue('green.500', 'green.600')
  const yellowProgressColor = useColorModeValue('yellow.400', 'yellow.500')
  const progressBackground = useColorModeValue('gray.200', 'gray.500')

  if (!component) return <ComponentUnavailable />
  if (profileError || profileLoading || !profile) return <PageStatus isLoading={profileLoading} isError={!!profileError} title="Profil" />

  if (!profile.loggedIn || profile.role === 'GUEST') {
    return <Navigate replace to="/" />
  }

  return (
    <CmschPage loginRequired>
      <Helmet title={component.title} />
      {component.messageBoxContent && (
        <Alert status="info" variant="left-accent" mt={5}>
          <AlertIcon />
          {component.messageBoxContent}
        </Alert>
      )}
      <PresenceAlert acquired={profile?.collectedTokenCount || 0} needed={profile?.minTokenToComplete || 0} mt={5} />

      {component?.showIncompleteProfile && (
        <Alert status={profile.profileIsComplete ? 'success' : 'error'} variant="left-accent" mb={5}>
          <AlertIcon />
          <Flex flexWrap="wrap" alignItems="center" w="full">
            <Box py={2}>
              {profile.profileIsComplete
                ? component.profileComplete
                : templateStringReplace(component.profileIncomplete, profile.incompleteTasks?.join(', '))}
            </Box>
            {!profile.profileIsComplete && !!config.task && (
              <Flex flex={1} justifyContent="end">
                <LinkButton href={AbsolutePaths.TASKS} ml={5} colorScheme="red">
                  {config.task.title}
                </LinkButton>
              </Flex>
            )}
          </Flex>
        </Alert>
      )}
      <Flex justifyContent="space-between" flexDirection={{ base: 'column', md: 'row' }}>
        <Box>
          {component.showFullName && <Heading>{profile.fullName}</Heading>}
          {component.showAlias && <Text fontSize="xl">Becenév: {profile.alias || 'nincs'}</Text>}
          {component.showNeptun && <Text fontSize="xl">Neptun: {profile.neptun || 'nincs'}</Text>}
          {component.showEmail && <Text fontSize="xl">E-mail: {profile.email || 'nincs'}</Text>}

          {component.showGuild && <Text fontSize="xl">Gárda: {GuildType[profile?.guild || 'UNKNOWN'] || 'nincs'}</Text>}
          {component.showMajor && <Text fontSize="xl">Szak: {profile.major || 'nincs'}</Text>}
          {component.showGroup && (
            <Text fontSize="xl">
              {component?.groupTitle}: {profile.groupName || 'nincs'}
            </Text>
          )}
        </Box>
        <VStack ml={{ base: 0, md: 'auto' }} mr={{ base: 'auto', md: 0 }} py={2} alignItems="stretch" mt={{ base: 5, md: 0 }}>
          {profile.role && RoleType[profile.role] >= RoleType.STAFF && (
            <LinkButton colorScheme={brandColor} href={`${API_BASE_URL}/admin/control`} external>
              Admin panel
            </LinkButton>
          )}
          {config?.groupselection && profile.groupSelectionAllowed && (
            <LinkButton colorScheme={brandColor} href={AbsolutePaths.CHANGE_GROUP}>
              {component?.groupTitle} módosítása
            </LinkButton>
          )}
          {component.aliasChangeEnabled && (
            <LinkButton colorScheme={brandColor} href={AbsolutePaths.CHANGE_ALIAS}>
              Becenév módosítása
            </LinkButton>
          )}
          <Button colorScheme={brandColor} variant="outline" onClick={onLogout}>
            Kijelentkezés
          </Button>
        </VStack>
      </Flex>
      {component.showGroup && <GroupLeaderContactList profile={profile} />}

      {profile.userMessage && (
        <>
          <Divider my={8} borderWidth={2} />
          <Markdown text={profile.userMessage} />
        </>
      )}

      {profile.groupMessage && (
        <>
          <Divider my={8} borderWidth={2} />
          <Markdown text={profile.groupMessage} />
        </>
      )}

      {component.showQr && profile.cmschId && (
        <>
          <Divider my={8} borderWidth={2} />
          <ProfileQR profile={profile} component={component} />
        </>
      )}
      {(component.showTasks || component.showRiddles || component.showTokens) && <Divider my={8} borderWidth={2} />}

      {component.showRaceStats && profile?.raceStat && (
        <Grid templateColumns={{ base: '1fr 1fr', sm: '1fr 2fr', md: '1fr 3fr' }}>
          <Text as="i">Mérés eredmény: </Text>
          <Text>
            <Text as="b">{profile?.raceStat}s</Text>
            {profile.racePlacement && <Text as="b" fontStyle="italic">{` (${profile.racePlacement}. helyezett)`}</Text>}
          </Text>
        </Grid>
      )}
      {component.showRaceStats && profile?.freestyleRaceStat && (
        <Grid templateColumns={{ base: '1fr 1fr', sm: '1fr 2fr', md: '1fr 3fr' }}>
          <Text as="i" whiteSpace="nowrap">
            Funky Mérés:{' '}
          </Text>
          <Text>
            <Text as="b" whiteSpace="nowrap">
              {profile.freestyleRaceStat}s
            </Text>
            {profile.freestyleRaceDescription && <Text as="b" fontStyle="italic">{` (${profile.freestyleRaceDescription})`}</Text>}
          </Text>
        </Grid>
      )}

      <Flex justify="center" alignItems="center" flexWrap="wrap">
        {component.showTasks && (
          <Center p={3}>
            <Flex direction="column" align="center">
              <Link
                href={AbsolutePaths.TASKS}
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: brandColor
                }}
              >
                {component.taskCounterName}
              </Link>
              <Box>
                <CircularProgress
                  color={yellowProgressColor}
                  size="10rem"
                  position="absolute"
                  value={submittedPercent(profile) + completedPercent(profile)}
                  trackColor={progressBackground}
                />
                <CircularProgress color={greenProgressColor} size="10rem" value={completedPercent(profile)} trackColor="transparent">
                  <CircularProgressLabel
                    color={submittedPercent(profile) + completedPercent(profile) === 0 ? 'gray.500' : greenProgressColor}
                    pb={submittedPercent(profile) !== 0 ? '2.9rem' : '0'}
                  >
                    {Math.round(completedPercent(profile))}%
                  </CircularProgressLabel>
                  {submittedPercent(profile) !== 0 && (
                    <>
                      <CircularProgressLabel>
                        <Box height="2px" backgroundColor={progressBackground} width="70%" mx="auto" borderRadius="20%" />
                      </CircularProgressLabel>
                      <CircularProgressLabel color={yellowProgressColor} pt="2.5rem">
                        {Math.round(submittedPercent(profile))}%
                      </CircularProgressLabel>
                    </>
                  )}
                </CircularProgress>
              </Box>
            </Flex>
          </Center>
        )}

        {component.showRiddles && (
          <Center p={3}>
            <Flex direction="column" align="center">
              <Link
                href={AbsolutePaths.RIDDLE}
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: brandColor
                }}
              >
                {component.riddleCounterName}
              </Link>
              <CircularProgress
                color={greenProgressColor}
                size="10rem"
                value={((profile?.completedRiddleCount || 0) / (profile?.totalRiddleCount || 0)) * 100}
                trackColor={progressBackground}
              >
                <CircularProgressLabel color={profile.completedRiddleCount === 0 ? 'gray.500' : greenProgressColor}>
                  {Math.round(((profile?.completedRiddleCount || 0) / (profile?.totalRiddleCount || 0)) * 100)}%
                </CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        )}
        {component.showTokens && (
          <Center p={3}>
            <Flex direction="column" align="center">
              <Link
                href={AbsolutePaths.TOKEN}
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: brandColor
                }}
              >
                {component.tokenCounterName}
              </Link>
              <CircularProgress
                color={greenProgressColor}
                size="10rem"
                value={((profile?.collectedTokenCount || 0) / (profile?.totalTokenCount || 0)) * 100}
                trackColor={progressBackground}
              >
                <CircularProgressLabel color={profile.collectedTokenCount === 0 ? 'gray.500' : greenProgressColor}>
                  {Math.round(((profile?.collectedTokenCount || 0) / (profile?.totalTokenCount || 0)) * 100)}%
                </CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        )}
      </Flex>
      {component.showGroupLeadersLocations && <Map />}
    </CmschPage>
  )
}

export default ProfilePage
