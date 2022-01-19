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
  Button,
  HStack
} from '@chakra-ui/react'
import * as React from 'react'
import { ProfileDTO, RoleType } from 'types/dto/profile'
import axios from 'axios'
import { API_BASE_URL } from 'utils/configurations'

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
    <Page {...props}>
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
  const [delay, setDelay] = React.useState<boolean>(false)

  React.useEffect(() => {
    //delay is needed to remove flickering of the skeleton
    setDelay(true)
    setTimeout(() => setDelay(false), 1000)
    axios.get<ProfileDTO>(`${API_BASE_URL}/api/profile`).then((res) => {
      if (res.status !== 200) {
        console.log(res)
        return
      }
      setProfile(res.data)
    })
  }, [setProfile])

  if (delay || profile?.fullName === undefined) return <ProfilePageSkeleton {...props} />

  return (
    <Page {...props}>
      <Heading>Profil</Heading>
      <Text fontSize="3xl">{profile?.fullName}</Text>
      <HStack justify="space-between">
        <Text fontSize="3xl">Tankör: {profile?.groupName || 'nincs'}</Text>
        {profile?.role && RoleType[profile.role] >= RoleType.STAFF && (
          <a href="/admin/control/">
            <Button as="span" colorScheme="red">
              Admin panel
            </Button>
          </a>
        )}
      </HStack>
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
