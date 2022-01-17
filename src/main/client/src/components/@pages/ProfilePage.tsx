import { Page } from '../@layout/Page'
import { Text, Flex, Heading, CircularProgress, CircularProgressLabel, Wrap, WrapItem, Center } from '@chakra-ui/react'

const challenges = [
  {
    name: 'Bucketlist',
    value: 40
  },
  {
    name: 'Riddle',
    value: 73
  },
  {
    name: 'QR kód',
    value: 17
  }
]

type ProfilePageProps = {}

export const ProfilePage: React.FC<ProfilePageProps> = (props) => {
  return (
    <Page {...props}>
      <Heading>Profil</Heading>
      <Text fontSize="3xl">Kovács Péter</Text>
      <Text marginBottom="10" fontSize="3xl">
        Tankör: I06
      </Text>
      <Wrap spacing="3rem" justify="center">
        {challenges.map((challenge) => (
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
