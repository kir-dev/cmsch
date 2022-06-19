import { VStack, Skeleton, Wrap, WrapItem, Center, Flex, SkeletonCircle } from '@chakra-ui/react'
import { CmschPage } from '../../../common-components/layout/CmschPage'

export const ProfilePageSkeleton = () => (
  <CmschPage loginRequired>
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
  </CmschPage>
)
