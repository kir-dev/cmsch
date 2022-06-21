import { Box, Collapse, Flex, Icon, IconButton, Image, useColorModeValue, useDisclosure } from '@chakra-ui/react'
import { DesktopNav } from './desktop/DesktopNav'
import { MobileNav } from './mobile/MobileNav'
import { Link } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { SidebarMenu } from './sidebar/SidebarMenu'

export const Navbar = () => {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const config = useConfigContext()
  const logoUrl = useColorModeValue(config?.components.style?.lightLogoUrl, config?.components.style?.darkLogoUrl)
  const menu = config?.menu || []
  const menuWithLogin = [...menu, LoginMenu]
  const btnRef = useRef(null)

  return (
    <Box mx="auto" w="full">
      <Flex
        minH={{ base: '3rem', md: '4.5rem' }}
        maxW={['100%', '100%', '56rem', '72rem']}
        py={2}
        px={{ base: 2, md: 4 }}
        alignItems="center"
        justifyContent="space-between"
      >
        <Flex>
          <IconButton
            ref={btnRef}
            onClick={onOpen}
            icon={<Icon as={FaBars} w={5} h={5} />}
            variant="ghost"
            aria-label="Navigáció megnyitása"
          />
        </Flex>
        <Flex justifyContent={{ base: 'center', md: 'start' }}>
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
