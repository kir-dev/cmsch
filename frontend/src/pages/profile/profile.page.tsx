import {
  Alert,
  AlertIcon,
  Box,
  Button,
  Center,
  CircularProgress,
  CircularProgressLabel,
  Flex,
  Heading,
  Link,
  Text,
  useColorModeValue,
  VStack
} from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { Navigate } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { Loading } from '../../common-components/Loading'
import { PresenceAlert } from '../../common-components/PresenceAlert'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { RoleType } from '../../util/views/profile.view'
import { ProfilePageSkeleton } from './components/ProfilePageSkeleton'
import { completedPercent, submittedPercent } from './util/percentFunctions'
import { AbsolutePaths } from '../../util/paths'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import templateStringReplace from '../../util/templateStringReplace'

type Props = {}

const ProfilePage = ({}: Props) => {
  const { onLogout, profile, profileLoading, profileError } = useAuthContext()
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
    sendMessage('Profil betöltése sikertelen! ' + profileError.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (!profile) {
    sendMessage('Profil betöltése sikertelen! A profil üres maradt. Keresd az oldal fejlesztőit a hiba kinyomozása érdekében!')
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  return (
    <CmschPage loginRequired>
      <Helmet title={component?.title} />
      <Flex justifyContent="space-between" flexDirection={{ base: 'column', sm: 'row' }}>
        <Box>
          {component?.showFullName && <Heading>{profile.fullName}</Heading>}
          {component?.showAlias && <Text fontSize="3xl">Becenév: {profile.alias || 'nincs'}</Text>}
          {component?.showNeptun && <Text fontSize="3xl">Neptun: {profile.neptun || 'nincs'}</Text>}
          {component?.showEmail && <Text fontSize="3xl">E-mail: {profile.email || 'nincs'}</Text>}
          {component?.showGroup && (
            <Text fontSize="3xl">
              {component?.groupTitle}: {profile.groupName || 'nincs'}
            </Text>
          )}
          {component?.showGuild && <Text fontSize="3xl">Gárda: {profile.guild || 'nincs'}</Text>}
          {component?.showMajor && <Text fontSize="3xl">Szak: {profile.major || 'nincs'}</Text>}
        </Box>
        <VStack py={2} alignItems="flex-end">
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
          <Button colorScheme="brand" variant="outline" onClick={onLogout}>
            Kijelentkezés
          </Button>
        </VStack>
      </Flex>
      {component?.messageBoxContent && (
        <Alert status="info" variant="left-accent" mt={5}>
          <AlertIcon />
          {component?.messageBoxContent}
        </Alert>
      )}
      {component?.showMinimumToken && (
        <PresenceAlert
          acquired={profile.tokens?.filter((token) => token.type === 'default').length}
          needed={profile.minTokenToComplete}
          mt={5}
        />
      )}

      <Alert status={profile.profileIsComplete ? 'success' : 'error'} variant="left-accent" mt={5}>
        <AlertIcon />
        <Flex flexWrap="wrap" alignItems="center">
          {profile.profileIsComplete
            ? component?.profileComplete
            : templateStringReplace(component?.profileIncomplete, profile?.incompleteTasks?.join(', '))}
          {!profile.profileIsComplete && (
            <LinkButton href={AbsolutePaths.TASKS} ml={5} colorScheme="red">
              Feladatok
            </LinkButton>
          )}
        </Flex>
      </Alert>

      <Flex justify="center" alignItems="center" flexWrap="wrap" mt="10">
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
                  trackColor={useColorModeValue('gray.200', 'gray.700')}
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
                trackColor={useColorModeValue('gray.200', 'gray.700')}
              >
                <CircularProgressLabel
                  color={profile.completedRiddleCount === 0 ? 'gray.500' : useColorModeValue('green.500', 'green.600')}
                >
                  {(profile.completedRiddleCount / profile.totalRiddleCount) * 100}%
                </CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        )}
        {component?.showQr && (
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
                {component?.tokenCounterName}
              </Link>
              <CircularProgress
                color={useColorModeValue('green.500', 'green.600')}
                size="10rem"
                value={(profile.collectedTokenCount / profile.totalTokenCount) * 100}
                trackColor={useColorModeValue('gray.200', 'gray.700')}
              >
                <CircularProgressLabel color={profile.collectedTokenCount === 0 ? 'gray.500' : useColorModeValue('green.500', 'green.600')}>
                  {(profile.collectedTokenCount / profile.totalTokenCount) * 100}%
                </CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        )}
      </Flex>
    </CmschPage>
  )
}

export default ProfilePage
