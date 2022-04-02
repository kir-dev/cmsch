import * as React from 'react'
import { Box, Flex, HStack, Icon, Image, Link, StackProps, Text, useColorModeValue, VStack, Wrap } from '@chakra-ui/react'
import { BUGREPORT_URL, KIRDEV_URL } from 'utils/configurations'
import { socialPages } from 'content/socialPages'
import { FaHeart } from 'react-icons/fa'
import { Container } from './Container'
import customTheme from '../../utils/customTheme'

type ImpressumWrapItemProps = {
  display: {
    base?: string
    md: string
  }
}
const ImpressumWrapItem: React.FC<ImpressumWrapItemProps> = ({ display }) => {
  return (
    <FooterWrapItem display={display}>
      <Flex justifyContent="center">
        <FooterBigImage src={`/img/${useColorModeValue('footer_logo.png', 'footer_logo_white.png')}`} />
      </Flex>
      <HStack spacing={1} justify="center">
        {socialPages.map((socialPage) => (
          <HStack as={Link} _hover={{ color: customTheme.colors.kirDev }} href={socialPage.href} isExternal key={socialPage.label}>
            <Icon as={socialPage.icon} boxSize="2rem" />
          </HStack>
        ))}
      </HStack>
      <Text marginBottom={3} align="center" fontSize="xl">
        <Link textColor={customTheme.colors.kirDev} href="/impresszum">
          Impresszum
        </Link>
      </Text>
      <Flex align="center">
        <Text>@ kir-dev [at] sch.bme.hu</Text>
        <Text>© 2022</Text>
      </Flex>
    </FooterWrapItem>
  )
}

export const Footer: React.FC = () => (
  <Box borderTopWidth={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.700')}>
    <Container>
      <Wrap justify="space-between" spacing={2} align="center">
        <FooterWrapItem>
          <FooterBigImage src="/img/communities/sssl.svg" filter={useColorModeValue('', 'invert(100%)')} />
          <HStack align="center">
            <Link
              isExternal
              fontSize="xl"
              _hover={{ color: customTheme.colors.kirDev, textDecorationLine: 'underline' }}
              href="https://sssl.sch.bme.hu"
            >
              Weboldal
            </Link>
            <Text>|</Text>
            <Link
              isExternal
              fontSize="xl"
              _hover={{ color: customTheme.colors.kirDev, textDecorationLine: 'underline' }}
              href="https://sssl.sch.bme.hu/contact"
            >
              Kapcsolat
            </Link>
          </HStack>
        </FooterWrapItem>
        <ImpressumWrapItem key="bigDisplay" display={{ base: 'none', md: 'block' }} />
        <FooterWrapItem>
          <Flex direction="row" align="center">
            <Text mr={2}>Made with</Text>
            <FaHeart color="red" size="1.5rem" />
            <Text ml={2}>by</Text>
          </Flex>
          <FooterBigImage src={useColorModeValue('/img/communities/kirdev.svg', '/img/communities/kirdev-white.svg')} />
          <HStack align="center">
            <Link isExternal fontSize="xl" _hover={{ color: customTheme.colors.kirDev, textDecorationLine: 'underline' }} href={KIRDEV_URL}>
              Weboldal
            </Link>
            <Text>|</Text>
            <Link
              isExternal
              fontSize="xl"
              _hover={{ color: customTheme.colors.kirDev, textDecorationLine: 'underline' }}
              href={BUGREPORT_URL}
            >
              Kapcsolat
            </Link>
          </HStack>
        </FooterWrapItem>
        <ImpressumWrapItem key="smallDisplay" display={{ md: 'none' }} />
      </Wrap>
    </Container>
  </Box>
)

const FooterWrapItem: React.FC<StackProps> = ({ children, ...props }) => {
  return (
    <VStack py={3} spacing={1} align="center" width={{ base: '100%', md: 'fit-content' }} {...props}>
      {children}
    </VStack>
  )
}

const FooterBigImage: React.FC<{ src: string; filter?: string }> = ({ src, filter = '' }) => {
  return <Image src={src} w="10rem" h="10rem" my={3} filter={filter} />
}
