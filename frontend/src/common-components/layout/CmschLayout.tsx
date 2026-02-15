import { Box, Flex } from '@chakra-ui/react'
import { type PropsWithChildren, useEffect } from 'react'
import { Navigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { ErrorBoundary } from '../../util/errorBoundary.tsx'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { EnableNotifications } from '../EnableNotifications'
import { Footer } from '../footer/Footer'
import { Navbar } from '../navigation/Navbar'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'

export const CmschLayout = ({ children }: PropsWithChildren) => {
  const config = useConfigContext()
  const { sendMessage } = useServiceContext()
  const component = config?.components?.app

  useEffect(() => {
    if (!component) sendMessage(l('component-unavailable'))
  }, [component, sendMessage])

  if (!component) {
    return <Navigate to={AbsolutePaths.ERROR} />
  }

  return (
    <>
      <title>{component.siteName || 'CMSch'}</title>
      <Flex direction="column" minHeight="100vh">
        <ScrollToTop />
        <Navbar />
        <Box flex={1} pb={20}>
          <ErrorBoundary>
            <Warning />
            <EnableNotifications />
            <ErrorBoundary>{children}</ErrorBoundary>
          </ErrorBoundary>
        </Box>
        <Footer />
      </Flex>
    </>
  )
}
