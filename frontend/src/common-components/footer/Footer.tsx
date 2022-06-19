import { Box, Flex, HStack, Image, Link, Text, useColorModeValue, Wrap } from '@chakra-ui/react'
import { FaHeart } from 'react-icons/fa'
import { CmschContainer } from '../layout/CmschContainer'
import { BUGREPORT_URL, KIRDEV_URL } from '../../util/configs/environment.config'
import { customTheme } from '../../util/configs/theme.config'
import Markdown from '../Markdown'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

export const Footer = () => {
  const config = useConfigContext()
  return (
    <Box borderTopWidth={1} borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.700')}>
      <CmschContainer>
        <Wrap justify="space-between" spacing={2} align="center">
          <Markdown text={config?.components.app.footerMessage} />
          <Flex align="center" flexWrap="wrap">
            <Flex direction="row" align="center">
              <Text mr={2}>Made with</Text>
              <FaHeart color="red" size="1.5rem" />
              <Text ml={2}>by</Text>
            </Flex>
            <Image src={useColorModeValue('/img/kirdev.svg', '/img/kirdev-white.svg')} w="10rem" h="10rem" my={3} />
            <HStack align="center">
              <Link
                isExternal
                fontSize="xl"
                _hover={{ color: customTheme.colors.brand, textDecorationLine: 'underline' }}
                href={KIRDEV_URL}
              >
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
          </Flex>
        </Wrap>
      </CmschContainer>
    </Box>
  )
}
