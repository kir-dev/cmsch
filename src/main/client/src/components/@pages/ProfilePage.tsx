import { Container } from '../@layout/Container'
import { Outlet } from 'react-router-dom'

import { Text, Flex, Heading, CircularProgress, CircularProgressLabel, Wrap, WrapItem, Center } from '@chakra-ui/react'

export function ProfilePage() {
  return (
    <Container>
      <Outlet />
      <Heading>Profil</Heading>
      <Text fontSize="3xl">Kovács Péter</Text>
      <Text marginBottom={10} fontSize="3xl">
        Tankör: I06
      </Text>
      <Wrap spacing="3rem" justify="center">
        <WrapItem>
          <Center w="10rem" h="12rem">
            <Flex direction={'column'} align={'center'}>
              <Text fontSize="3xl">Bucketlist</Text>
              <CircularProgress value={40} color="green.400" size="10rem">
                <CircularProgressLabel>40%</CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        </WrapItem>
        <WrapItem>
          <Center w="10rem" h="12rem">
            <Flex direction={'column'} align={'center'}>
              <Text fontSize="3xl">Riddle</Text>
              <CircularProgress value={73} color="green.400" size="10rem">
                <CircularProgressLabel>73%</CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        </WrapItem>
        <WrapItem>
          <Center w="10rem" h="12rem">
            <Flex direction={'column'} align={'center'}>
              <Text fontSize="3xl">QR kód</Text>
              <CircularProgress value={17} color="green.400" size="10rem">
                <CircularProgressLabel>17%</CircularProgressLabel>
              </CircularProgress>
            </Flex>
          </Center>
        </WrapItem>
      </Wrap>
    </Container>
  )
}
