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
import { collectChallengeDetails } from './util/challengeFunctions'
import { submittedPercent, completedPercent } from './util/percentFunctions'

type Props = {}

const ProfilePage = ({}: Props) => {
  const { onLogout, profile, profileLoading, profileError } = useAuthContext()
  const { sendMessage } = useServiceContext()

  if (profileLoading) {
    return (
      <Loading>
        <ProfilePageSkeleton />
      </Loading>
    )
  }

  if (profileError) {
    sendMessage('Profil betöltése sikertelen! ' + profileError.message)
    return <Navigate replace to="/error" />
  }

  if (!profile) {
    sendMessage('Profil betöltése sikertelen! A profil üres maradt. Keresd az oldal fejlesztőit a hiba kinyomozása érdekében!')
    return <Navigate replace to="/error" />
  }

  return (
    <CmschPage loginRequired>
      <Helmet title="Profil" />
      <Flex justifyContent="space-between" flexDirection={{ base: 'column', sm: 'row' }}>
        <Box>
          <Heading>{profile.fullName}</Heading>
          <Text fontSize="3xl">Tankör: {profile.groupName || 'nincs'}</Text>
        </Box>
        <VStack py={2} alignItems="flex-end">
          {profile.role && RoleType[profile.role] >= RoleType.STAFF && (
            <LinkButton colorScheme="brand" href={`${API_BASE_URL}/admin/control`} external>
              Admin panel
            </LinkButton>
          )}
          {profile.groupSelectionAllowed && (
            <LinkButton colorScheme="brand" href="/profil/tankor-modositas">
              Tankör módosítása
            </LinkButton>
          )}
          <Button colorScheme="brand" variant="outline" onClick={onLogout}>
            Kijelentkezés
          </Button>
        </VStack>
      </Flex>
      {!profile.groupSelectionAllowed && (
        <Alert status="info" variant="left-accent" mt={5}>
          <AlertIcon />
          Ha a tanköröd nem helyes, akkor az infópultnál állíttasd át a megfelelőre! A sikeres tanköri jelenlétről csak a jelzett tankör
          tankörvezét fogjuk értesíteni.
        </Alert>
      )}
      <PresenceAlert
        acquired={profile.tokens.filter((token) => token.type === 'default').length}
        needed={profile.minTokenToComplete}
        mt={3}
      />

      <Flex justify="center" alignItems="center" flexWrap="wrap" mt="10">
        <Center p={3}>
          <Flex direction="column" align="center">
            <Link
              href="/bucketlist"
              fontSize="3xl"
              fontWeight={500}
              _hover={{
                textDecoration: 'none',
                color: useColorModeValue('brand.500', 'brand.600')
              }}
            >
              Bucketlist
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
                color={useColorModeValue('brand.500', 'brand.600')}
                size="10rem"
                value={completedPercent(profile)}
                trackColor="transparent"
              >
                <CircularProgressLabel
                  color={
                    submittedPercent(profile) + completedPercent(profile) === 0 ? 'gray.500' : useColorModeValue('brand.500', 'brand.600')
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

        {collectChallengeDetails(profile).map((challenge) => (
          <Center p={3}>
            <Flex direction="column" align="center">
              <Link
                href={challenge.link}
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: useColorModeValue('brand.500', 'brand.600')
                }}
              >
                {challenge.name}
              </Link>
              <CircularProgress
                color={useColorModeValue('brand.500', 'brand.600')}
                size="10rem"
                value={challenge.percentage}
                trackColor={useColorModeValue('gray.200', 'gray.700')}
              >
                <CircularProgressLabel color={challenge.completed === 0 ? 'gray.500' : useColorModeValue('brand.500', 'brand.600')}>
                  {Math.round(challenge.percentage)}%
                </CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        ))}
      </Flex>
    </CmschPage>
  )
}

export default ProfilePage
