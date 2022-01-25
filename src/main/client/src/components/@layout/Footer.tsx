import * as React from 'react'
import { Box, Container, Link, Image, useColorModeValue, Wrap, WrapItem, Center, Flex, HStack, Text, Icon } from '@chakra-ui/react'
import { BUGREPORT_URL, KIRDEV_ORANGE, KIRDEV_URL } from 'utils/configurations'
import { socialPages } from 'content/socialPages'
import { Paragraph } from 'components/@commons/Basics'
import { FaHeart } from 'react-icons/fa'

type impresszumWrapItemProps = {
  display: {
    base?: string
    lg: string
  }
  key: string
}
const ImpresszumWrapItem: React.FC<impresszumWrapItemProps> = ({ display, key }) => {
  return (
    <WrapItem key={key} display={display}>
      <Center w="20rem" h="11rem">
        <Flex direction="column" align="center">
          <Box marginBottom="3rem" align="center">
            <Paragraph>Ha érdklődsz az oldal iránt:</Paragraph>
            <Link fontSize="xl" textColor={KIRDEV_ORANGE} href="/impresszum">
              Impresszum
            </Link>
          </Box>
          <Box align="center">
            <Text>@ kir-dev [kukac] sch.bme.hu</Text>
            <Text>© 2022</Text>
          </Box>
        </Flex>
      </Center>
    </WrapItem>
  )
}

export const Footer: React.FC = () => (
  <Box borderTopWidth={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.700')}>
    <Container maxW="6xl" py={4}>
      <Wrap justify="center" spacing="3rem" align="center">
        <WrapItem key="sssl">
          <Center w="20rem" h="26rem">
            <Flex direction="column" align="center">
              <Flex direction="row">
                <Text mb="1rem">Az eseméy szervezője: </Text>
              </Flex>
              <Image mb="2rem" src="/img/communities/sssl.svg" w="15rem" h="15rem"></Image>
              <Flex direction="column">
                <Flex direction="column" align="flex-end">
                  {socialPages.map((sociaPage) => (
                    <HStack pb={2} as={Link} _hover={{ color: KIRDEV_ORANGE }} href={sociaPage.href} isExternal key={sociaPage.label}>
                      <Text>{sociaPage.label}</Text>
                      <Icon as={sociaPage.icon} boxSize="1.5rem" />
                    </HStack>
                  ))}
                </Flex>
              </Flex>
            </Flex>
          </Center>
        </WrapItem>
        <ImpresszumWrapItem key="bigDisplay" display={{ base: 'none', lg: 'block' }} />
        <WrapItem key="kirdev">
          <Center w="20rem" h="20rem">
            <Flex direction="column" align="center">
              <Flex direction="row">
                <Text mr="0.5rem">Made with </Text>
                <FaHeart color="red" size="1.5rem"></FaHeart>
                <Text ml="0.5rem"> by</Text>
              </Flex>
              <Image src="/img/communities/kirdev.svg" w="15rem" h="15rem"></Image>
              <Box>
                <Link isExternal fontSize="xl" _hover={{ color: KIRDEV_ORANGE, textDecorationLine: 'underline' }} href={KIRDEV_URL}>
                  Kir-Dev
                </Link>
                {' | '}
                <Link isExternal fontSize="xl" _hover={{ color: KIRDEV_ORANGE, textDecorationLine: 'underline' }} href={BUGREPORT_URL}>
                  Contact
                </Link>
              </Box>
            </Flex>
          </Center>
        </WrapItem>
        <ImpresszumWrapItem key="smallDisplay" display={{ lg: 'none' }} />
      </Wrap>
    </Container>
  </Box>
)
