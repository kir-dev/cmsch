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
  Box
} from '@chakra-ui/react'
import * as React from 'react'
import { ProfileDTO, RoleType } from 'types/dto/profile'
import axios from 'axios'
import { API_BASE_URL } from 'utils/configurations'
import { Loading } from '../../utils/Loading'
import { LinkButton } from '../@commons/LinkButton'
import { useServiceContext } from '../../utils/useServiceContext'
import { useAuthContext } from 'utils/useAuthContext'

const challenges = (profile: ProfileDTO) => [
  {
    name: 'Bucketlist',
    value: profile?.submittedAchievementCount,
    total: profile?.totalAchievementCount
  },
  {
    name: 'Riddle',
    value: profile?.completedRiddleCount,
    total: profile?.totalRiddleCount
  },
  {
    name: 'QR kód',
    value: profile?.tokens?.length,
    total: profile?.minTokenToComplete
  }
]

type ProfilePageProps = {}

//Had to create a seperate skeleton layout so it wouldn't look strange
export const ProfilePageSkeleton: React.FC<ProfilePageProps> = (props) => {
  return (
    <Page {...props} loginRequired title="Profil">
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
          <LinkButton href="#" colorScheme="brand" variant="outline" onClick={logout}>
            Kijelentkezés
          </LinkButton>
        </VStack>
      </Flex>

      <Wrap spacing="3rem" justify="center" mt="10">
        {challenges(profile!).map((challenge) => (
          <WrapItem key={challenge.name}>
            <Center w="10rem" h="12rem">
              <Flex direction="column" align="center">
                <Text fontSize="3xl">{challenge.name}</Text>
                <CircularProgress value={challenge.value} color="brand.400" size="10rem">
                  <CircularProgressLabel>{challenge.value}%</CircularProgressLabel>
                </CircularProgress>
              </Flex>
            </Center>
          </WrapItem>
        ))}
      </Wrap>
    </Page>
  )
}
