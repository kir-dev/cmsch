import { Page } from '../@layout/Page'
import {
  Text,
  Flex,
  Heading,
  CircularProgress,
  CircularProgressLabel,
  Wrap,
  WrapItem,
  Center,
  Skeleton,
  VStack,
  SkeletonCircle,
  Box,
  Button,
  useColorModeValue,
  Alert,
  AlertIcon,
  Link
} from '@chakra-ui/react'
import * as React from 'react'
import { ProfileDTO, RoleType } from 'types/dto/profile'
import axios from 'axios'
import { API_BASE_URL } from 'utils/configurations'
import { Loading } from '../../utils/Loading'
import { LinkButton } from '../@commons/LinkButton'
import { useServiceContext } from '../../utils/useServiceContext'
import { useAuthContext } from 'utils/useAuthContext'
import { Helmet } from 'react-helmet'
import { PresenceAlert } from 'components/@commons/PresenceAlert'

const challenges = (profile: ProfileDTO) => [
  {
    name: 'Riddle',
    completed: profile?.completedRiddleCount,
    total: profile?.totalRiddleCount,
    link: '/riddleok'
  },
  {
    name: 'QR kód',
    completed: profile?.tokens?.length,
    total: profile?.minTokenToComplete,
    link: '/qr'
  }
]

type ProfilePageProps = {}

function submittedPercent(profile: ProfileDTO) {
  return (profile.submittedAchievementCount / profile.totalAchievementCount) * 100
}

function completedPercent(profile: ProfileDTO) {
  return (profile.completedAchievementCount / profile.totalAchievementCount) * 100
}

//Had to create a seperate skeleton layout so it wouldn't look strange
export const ProfilePageSkeleton: React.FC<ProfilePageProps> = (props) => {
  return (
    <Page {...props} loginRequired>
      <VStack align="flex-start" mb="14" mt={6}>
        <Skeleton h={12} w={['40%', null, null, '15%']} />
        <Skeleton h={10} w={['50%', null, null, '20%']} />
        <Skeleton h={10} w={['60%', null, null, '25%']} />
      </VStack>
      <Wrap spacing="3rem" justify="center" mt={3}>
        {[0, 1, 2].map((challenge) => (
          <WrapItem key={challenge}>
            <Center w="10rem" h="12rem">
              <Flex direction="column" align="center">
                <Skeleton mb={3} w="90%" h={10} />
                <SkeletonCircle size="10rem" />
              </Flex>
            </Center>
          </WrapItem>
        ))}
      </Wrap>
    </Page>
  )
}

export const ProfilePage: React.FC<ProfilePageProps> = (props) => {
  const [profile, setProfile] = React.useState<ProfileDTO | undefined>(undefined)
  const { throwError } = useServiceContext()
  const { logout } = useAuthContext()

  React.useEffect(() => {
    axios
      .get<ProfileDTO>(`${API_BASE_URL}/api/profile`)
      .then((res) => {
        if (res.status === 200) {
          setProfile(res.data)
        }
      })
      .catch(() => {
        throwError('Nem sikerült lekérdezni a profilt.')
      })
  }, [setProfile])

  if (profile === undefined)
    return (
      <Loading>
        <ProfilePageSkeleton {...props} />
      </Loading>
    )

  return (
    <Page {...props} loginRequired>
      <Helmet title="Profil" />
      <Flex justifyContent="space-between" flexDirection={{ base: 'column', sm: 'row' }}>
        <Box>
          <Heading>{profile?.fullName}</Heading>
          <Text fontSize="3xl">Tankör: {profile?.groupName || 'nincs'}</Text>
        </Box>
        <VStack py={2} alignItems="flex-end">
          {profile?.role && RoleType[profile.role] >= RoleType.STAFF && (
            <LinkButton colorScheme="brand" href="/admin/control" external>
              Admin panel
            </LinkButton>
          )}
          <Button colorScheme="brand" variant="outline" onClick={logout}>
            Kijelentkezés
          </Button>
        </VStack>
      </Flex>
      <Alert status="info" variant="left-accent" mt={5}>
        <AlertIcon />
        Ha a tanköröd nem helyes, akkor az infópultnál állíttasd át a megfelelőre! A sikeres tanköri jelenlétről csak a jelzett tankör
        tankörvezét fogjuk értesíteni.
      </Alert>
      <PresenceAlert acquired={profile.totalTokenCount} needed={profile.minTokenToComplete} mt={3} />

      <Wrap spacing="3rem" justify="center" mt="10">
        <WrapItem key="achivement">
          <Center w="10rem" h="12rem">
            <Flex direction="column" align="center">
              <Link
                href="/bucketlist"
                fontSize="3xl"
                fontWeight={500}
                _hover={{
                  textDecoration: 'none',
                  color: 'brand.500'
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
                        ></Box>
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
        </WrapItem>

        {challenges(profile!).map((challenge) => (
          <WrapItem key={challenge.name}>
            <Center w="10rem" h="12rem">
              <Flex direction="column" align="center">
                <Link
                  href={challenge.link}
                  fontSize="3xl"
                  fontWeight={500}
                  _hover={{
                    textDecoration: 'none',
                    color: 'brand.500'
                  }}
                >
                  {challenge.name}
                </Link>
                <CircularProgress
                  color={useColorModeValue('brand.500', 'brand.600')}
                  size="10rem"
                  value={(challenge.completed / challenge.total) * 100}
                  trackColor={useColorModeValue('gray.200', 'gray.700')}
                >
                  <CircularProgressLabel color={challenge.completed === 0 ? 'gray.500' : useColorModeValue('brand.500', 'brand.600')}>
                    {Math.round((challenge.completed / challenge.total) * 100)}%
                  </CircularProgressLabel>
                </CircularProgress>
              </Flex>
            </Center>
          </WrapItem>
        ))}
      </Wrap>
    </Page>
  )
}
