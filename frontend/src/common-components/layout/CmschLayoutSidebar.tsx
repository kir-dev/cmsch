import { Box, Flex } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useLocation } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { HasChildren } from '../../util/react-types.util'
import { Footer } from '../footer/Footer'
import { MinimalisticFooter } from '../footer/MinimalisticFooter'
import { NavbarWithSidebar } from '../navigation/NavbarWithSidebar'
import { SidebarFixed } from '../navigation/sidebar/SidebarFixed'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'

type Props = {
  background?: string
  headingTitle?: string
} & HasChildren

export const CmschLayoutSidebar = ({ background, headingTitle, children }: Props) => {
  const config = useConfigContext()
  const location = useLocation()
  if (!headingTitle) {
    headingTitle = config?.menu.find((menu) => menu.url === location.pathname)?.name

    if (config && !headingTitle) {
      const keyFromPathname = location.pathname.substring(1)
      const componentTitle = (config?.components as any)?.[keyFromPathname]?.['title']
      if (componentTitle) {
        headingTitle = componentTitle
      }
    }

    if (location.pathname === config?.components.app.defaultComponent) headingTitle = 'Kezdőlap'
  }

  return (
    <>
      <Helmet
        titleTemplate={`%s | ${config?.components.app.siteName || 'CMSch'}`}
        defaultTitle={config?.components.app.siteName || 'CMSch'}
      />
      <Flex direction="row" minHeight="100vh">
        <SidebarFixed display={{ base: 'none', '2xl': 'inherit' }} position="fixed" width="20rem" h="full" />
        <Flex direction="column" minHeight="100vh" flex={1} ml={{ base: 0, '2xl': '20rem' }}>
          <ScrollToTop />
          <NavbarWithSidebar headingTitle={headingTitle} />
          <Box background={background} flex={1} pb={20}>
            <Warning />
            {children}
          </Box>
          {config?.components.app.minimalisticFooter ? <MinimalisticFooter /> : <Footer />}
        </Flex>
      </Flex>
    </>
  )
}
