import { Box, Flex } from '@chakra-ui/react'
import * as React from 'react'
import { PropsWithChildren } from 'react'
import { Helmet } from 'react-helmet-async'
import { Navigate } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import CurrentEvent from '../../pages/events/components/CurrentEvent'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import CurrentEventCard from '../CurrentEventCard'
import { Footer } from '../footer/Footer'
import { Navbar } from '../navigation/Navbar'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'

interface CmschLayoutProps extends PropsWithChildren {
  background?: string
}

export const CmschLayout = ({ background, children }: CmschLayoutProps) => {
  const config = useConfigContext()
  const { sendMessage } = useServiceContext()
  const component = config?.components.app

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
        <Footer />
      </Flex>
    </>
  )
}
