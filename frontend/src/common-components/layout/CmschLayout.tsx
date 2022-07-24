import { Box, Flex, useDisclosure } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { Footer } from '../footer/Footer'
import { HasChildren } from '../../util/react-types.util'
import { Navbar } from '../navigation/Navbar'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { SidebarFixed } from '../navigation/sidebar/SidebarFixed'
import { useLocation } from 'react-router-dom'

type Props = {
  background?: string
  headingTitle?: string
} & HasChildren

export const CmschLayout = ({ background, headingTitle, children }: Props) => {
  const config = useConfigContext()
  const location = useLocation()
  if (!headingTitle) {
    headingTitle = config?.menu.find((menu) => menu.url === location.pathname)?.name
    if (location.pathname === config?.components.app.defaultComponent) headingTitle = 'Kezdőlap'
  }

  return (
    <>
      <Helmet
        titleTemplate={`%s | ${config?.components.app.siteName || 'CMSch'}`}
        defaultTitle={config?.components.app.siteName || 'CMSch'}
      />
      <Flex direction="row" minHeight="100vh">
        <SidebarFixed display={{ base: 'none', '2xl': 'inherit' }} />
        <Flex direction="column" minHeight="100vh" flex={1}>
          <ScrollToTop />
          <Navbar headingTitle={headingTitle} />
          <Box background={background} flex={1} pb={20}>
            <Warning />
            {children}
          </Box>
          <Footer />
        </Flex>
      </Flex>
    </>
  )
}
