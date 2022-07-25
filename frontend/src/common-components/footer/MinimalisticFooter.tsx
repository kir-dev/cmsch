import { Box, Center, Flex, Image, useColorModeValue } from '@chakra-ui/react'
import { CmschContainer } from '../layout/CmschContainer'
import Markdown from '../Markdown'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

export const MinimalisticFooter = () => {
  const config = useConfigContext()
  const component = config?.components.app
  return (
    <Box borderStyle="solid" borderColor={useColorModeValue('gray.200', 'gray.700')}>
      <CmschContainer>
        {component?.footerMessage && (
          <Center flexDirection="column" px="4" py="4" mx="auto" maxWidth="100%">
            <Markdown text={component?.footerMessage} />
          </Center>
        )}
        <Flex justify="center" align="center" flexDirection="row">
          {component?.hostLogo && <Image src={component?.hostLogo} maxW="10rem" maxH="10rem" mr={3} alt={component?.hostAlt} />}
          <Image src={useColorModeValue('/img/kirdev.svg', '/img/kirdev-white.svg')} maxW="10rem" maxH="10rem" ml={3} />
        </Flex>
      </CmschContainer>
    </Box>
  )
}
