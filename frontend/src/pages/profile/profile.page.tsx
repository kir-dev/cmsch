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
  Heading,
  Link,
  Text,
  useColorModeValue,
  VStack
} from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { Loading } from '../../common-components/Loading'
import { PresenceAlert } from '../../common-components/PresenceAlert'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { GuildType, RoleType } from '../../util/views/profile.view'
import { ProfilePageSkeleton } from './components/ProfilePageSkeleton'
import { completedPercent, submittedPercent } from './util/percentFunctions'
import { AbsolutePaths } from '../../util/paths'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import templateStringReplace from '../../util/templateStringReplace'
import React, { useEffect } from 'react'
import { GroupComponent } from './components/Group'
import { ProfileQR } from './components/ProfileQR'
import { l } from '../../util/language'
import Markdown from '../../common-components/Markdown'

const MapContainer = React.lazy(() => import('./components/MapContainer'))

type Props = {}

const ProfilePage = ({}: Props) => {
  const { onLogout, profile, profileLoading, profileError, refetch } = useAuthContext()

  // The currentUser query is define in the Auth context, so it doesn't automatically run it when the profile page loads
  //(since the context provider component is already mounted)
  useEffect(() => {
    refetch()
  }, [])

  const { sendMessage } = useServiceContext()
  const config = useConfigContext()
  const component = config?.components.profile

  if (profileLoading && !profile) {
    return (
      <Loading>
        <ProfilePageSkeleton />
      </Loading>
    )
  }

  if (profileError) {
    sendMessage(l('profile-load-failed') + profileError.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (!profile) {
    sendMessage(l('profile-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (!profile.loggedIn || profile.role === 'GUEST') {
    return <Navigate replace to="/" />
  }

  return (
    <CmschPage loginRequired>
      <Helmet title={component?.title} />
      {component?.messageBoxContent && (
        <Alert status="info" variant="left-accent" mt={5}>
          <AlertIcon />
          {component?.messageBoxContent}
        </Alert>
      )}
      <PresenceAlert acquired={profile.collectedTokenCount} needed={profile.minTokenToComplete} mt={5} />

      {component?.showIncompleteProfile && (
        <Alert status={profile.profileIsComplete ? 'success' : 'error'} variant="left-accent" mb={5}>
          <AlertIcon />
          <Flex flexWrap="wrap" alignItems="center" w="full">
            <Box py={2}>
              {profile.profileIsComplete
                ? component?.profileComplete
                : templateStringReplace(component?.profileIncomplete, profile?.incompleteTasks?.join(', '))}
            </Box>
            {!profile.profileIsComplete && (
              <Flex flex={1} justifyContent="end">
                <LinkButton href={AbsolutePaths.TASKS} ml={5} colorScheme="red">
                  Feladatok
                </LinkButton>
              </Flex>
            )}
          </Flex>
        </Alert>
      )}
      <Flex justifyContent="space-between" flexDirection={{ base: 'column', md: 'row' }}>
        <Box>
          {component?.showFullName && <Heading>{profile.fullName}</Heading>}
          {component?.showAlias && <Text fontSize="xl">Becenév: {profile.alias || 'nincs'}</Text>}
          {component?.showNeptun && <Text fontSize="xl">Neptun: {profile.neptun || 'nincs'}</Text>}
          {component?.showEmail && <Text fontSize="xl">E-mail: {profile.email || 'nincs'}</Text>}

          {component?.showGuild && <Text fontSize="xl">Gárda: {GuildType[profile.guild] || 'nincs'}</Text>}
          {component?.showMajor && <Text fontSize="xl">Szak: {profile.major || 'nincs'}</Text>}
          {component?.showGroup && <GroupComponent profile={profile} />}
        </Box>
        <VStack py={2} alignItems={{ base: 'flex-start', md: 'flex-end' }} mt={{ base: 5, md: 0 }}>
          {profile.role && RoleType[profile.role] >= RoleType.STAFF && (
            <LinkButton colorScheme="brand" href={`${API_BASE_URL}/admin/control`} external>
              Admin panel
            </LinkButton>
          )}
          {profile.groupSelectionAllowed && (
            <LinkButton colorScheme="brand" href={`${AbsolutePaths.PROFILE}/tankor-modositas`}>
              {component?.groupTitle} módosítása
            </LinkButton>
          )}
          {component?.aliasChangeEnabled && (
            <LinkButton colorScheme="brand" href={`${AbsolutePaths.PROFILE}/change-alias`}>
              Becenév módosítása
            </LinkButton>
          )}
          <Button colorScheme="brand" variant="outline" onClick={onLogout}>
            Kijelentkezés
          </Button>
        </VStack>
      </Flex>

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

      {component?.showQr && <ProfileQR profile={profile} component={component} />}
      {(component?.showTasks || component?.showRiddles || component?.showTokens) && <Divider my={10} borderWidth={2} />}
      <Flex justify="center" alignItems="center" flexWrap="wrap">
        {component?.showTasks && (
          <Center p={3}>
            <Flex direction="column" align="center">
              <Link
                href={AbsolutePaths.TASKS}
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: useColorModeValue('brand.500', 'brand.600')
                }}
              >
                {component?.taskCounterName}
              </Link>
              <Box>
                <CircularProgress
                  color={useColorModeValue('yellow.400', 'yellow.500')}
                  size="10rem"
                  position="absolute"
                  value={submittedPercent(profile) + completedPercent(profile)}
                  trackColor={useColorModeValue('gray.200', 'gray.500')}
                />
                <CircularProgress
                  color={useColorModeValue('green.500', 'green.600')}
                  size="10rem"
                  value={completedPercent(profile)}
                  trackColor="transparent"
                >
                  <CircularProgressLabel
                    color={
                      submittedPercent(profile) + completedPercent(profile) === 0 ? 'gray.500' : useColorModeValue('green.500', 'green.600')
                    }
                    pb={submittedPercent(profile) !== 0 ? '2.9rem' : '0'}
                  >
                    {Math.round(completedPercent(profile))}%
                  </CircularProgressLabel>
                  {submittedPercent(profile) !== 0 && (
                    <>
                      <CircularProgressLabel>
                        <Box
                          height="2px"
                          backgroundColor={useColorModeValue('gray.200', 'gray.700')}
                          width="70%"
                          mx="auto"
                          borderRadius="20%"
                        />
                      </CircularProgressLabel>
                      <CircularProgressLabel color={useColorModeValue('yellow.400', 'yellow.500')} pt="2.5rem">
                        {Math.round(submittedPercent(profile))}%
                      </CircularProgressLabel>
                    </>
                  )}
                </CircularProgress>
              </Box>
            </Flex>
          </Center>
        )}

        {component?.showRiddles && (
          <Center p={3}>
            <Flex direction="column" align="center">
              <Link
                href={AbsolutePaths.RIDDLE}
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: useColorModeValue('brand.500', 'brand.600')
                }}
              >
                {component?.riddleCounterName}
              </Link>
              <CircularProgress
                color={useColorModeValue('green.500', 'green.600')}
                size="10rem"
                value={(profile.completedRiddleCount / profile.totalRiddleCount) * 100}
                trackColor={useColorModeValue('gray.200', 'gray.500')}
              >
                <CircularProgressLabel
                  color={profile.completedRiddleCount === 0 ? 'gray.500' : useColorModeValue('green.500', 'green.600')}
                >
                  {Math.round((profile.completedRiddleCount / profile.totalRiddleCount) * 100)}%
                </CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        )}
        {component?.showTokens && (
          <Center p={3}>
            <Flex direction="column" align="center">
              <Link
                href={AbsolutePaths.TOKEN}
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: useColorModeValue('brand.500', 'brand.600')
                }}
              >
                {component?.tokenCounterName}
              </Link>
              <CircularProgress
                color={useColorModeValue('green.500', 'green.600')}
                size="10rem"
                value={(profile.collectedTokenCount / profile.totalTokenCount) * 100}
                trackColor={useColorModeValue('gray.200', 'gray.500')}
              >
                <CircularProgressLabel color={profile.collectedTokenCount === 0 ? 'gray.500' : useColorModeValue('green.500', 'green.600')}>
                  {Math.round((profile.collectedTokenCount / profile.totalTokenCount) * 100)}%
                </CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        )}
      </Flex>
      {config?.components.profile.showGroupLeadersLocations && <MapContainer />}
    </CmschPage>
  )
}

export default ProfilePage
