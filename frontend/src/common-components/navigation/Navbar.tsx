import { Box, Flex, Heading, Icon, IconButton, Image, useBreakpointValue, useColorModeValue, useDisclosure } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { SidebarMenu } from './sidebar/SidebarMenu'
import { useRef } from 'react'
import { FaBars } from 'react-icons/fa'

type Props = {
  headingTitle?: string
}

export const Navbar = ({ headingTitle }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const config = useConfigContext()
  const logoUrl = useColorModeValue(config?.components.style?.lightLogoUrl, config?.components.style?.darkLogoUrl)
  const btnRef = useRef(null)

  return (
    <Box mx="auto" w="full">
      <Flex minH={{ base: '3rem', md: '4.5rem' }} py={2} px={{ base: 2, md: 4 }} alignItems="center" justifyContent="space-between">
        <Flex>
          {useBreakpointValue({ base: true, '2xl': false }) ? (
            <IconButton
              ref={btnRef}
              onClick={onOpen}
              icon={<Icon as={FaBars} w={5} h={5} />}
              variant="ghost"
              aria-label="Navigáció megnyitása"
              display={{ base: 'inherit', '2xl': 'none' }}
            />
          ) : (
            <Heading mt={3}>{headingTitle}</Heading>
          )}
        </Flex>
        <Flex justifyContent={{ base: 'center', md: 'start' }} display={{ base: 'inherit', '2xl': 'none' }}>
          <Link to="/">
            {logoUrl ? <Image maxH={16} maxW={16} src={logoUrl} alt="CMSch" /> : <Heading>{config?.components.app.siteName}</Heading>}
          </Link>
        </Flex>
        <Flex justifyContent="flex-end">
          <ColorModeSwitcher />
        </Flex>
      </Flex>
      <SidebarMenu isOpen={isOpen} onClose={onClose} openerButtonRef={btnRef} />
    </Box>
  )
}
