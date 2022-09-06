import { Box, Flex } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { HasChildren } from '../../util/react-types.util'
import { Footer } from '../footer/Footer'
import { MinimalisticFooter } from '../footer/MinimalisticFooter'
import { Navbar } from '../navigation/Navbar'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'

type Props = {
  background?: string
} & HasChildren

export const CmschLayout = ({ background, children }: Props) => {
  const config = useConfigContext()
  return (
    <>
      <Helmet
        titleTemplate={`%s | ${config?.components.app.siteName || 'CMSch'}`}
        defaultTitle={config?.components.app.siteName || 'CMSch'}
      />
      <Flex direction="column" minHeight="100vh">
        <ScrollToTop />
        <Navbar />
        <Box background={background} flex={1} pb={20}>
          <Warning />
          {children}
        </Box>
        {config?.components.app.minimalisticFooter ? <MinimalisticFooter /> : <Footer />}
      </Flex>
    </>
  )
}
