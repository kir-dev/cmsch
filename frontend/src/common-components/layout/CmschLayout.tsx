import { Box, Flex } from '@chakra-ui/react'
import * as React from 'react'
import { PropsWithChildren } from 'react'
import { Helmet } from 'react-helmet-async'
import { Footer } from '../footer/Footer'
import { Navbar } from '../navigation/Navbar'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { MinimalisticFooter } from '../footer/MinimalisticFooter'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { Navigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'

interface CmschLayoutProps extends PropsWithChildren {
  background?: string
}

export const CmschLayout = ({ background, children }: CmschLayoutProps) => {
  const config = useConfigContext()
  const { sendMessage } = useServiceContext()
  const component = config?.components.app
  const footer = config?.components.footer

  if (!component) {
    sendMessage(l('component-unavailable'))
    return <Navigate to={AbsolutePaths.ERROR} />
  }

  return (
    <>
      <Helmet titleTemplate={`%s | ${component.siteName || 'CMSch'}`} defaultTitle={component.siteName || 'CMSch'} />
      <Flex direction="column" minHeight="100vh">
        <ScrollToTop />
        <Navbar />
        <Box background={background} flex={1} pb={20}>
          <Warning />
          {children}
        </Box>
        {footer?.minimalisticFooter ? <MinimalisticFooter /> : <Footer />}
      </Flex>
    </>
  )
}
