import { Box, Flex } from '@chakra-ui/react'
import { Warning } from 'components/@commons/Warning'
import * as React from 'react'
import { Helmet } from 'react-helmet'
import { APP_NAME } from 'utils/configurations'
import { Footer } from './Footer'
import { Navbar } from './navigation/Navbar'
import ScrollToTop from '../../utils/ScrollToTop'
import { GroupSelectionWarning } from '../@commons/GroupSelectionWarning'

type IndexLayoutProps = {
  background?: string
}

export const IndexLayout: React.FC<IndexLayoutProps> = ({ background, children }) => (
  <>
    <Helmet titleTemplate={`%s | ${APP_NAME}`} defaultTitle={APP_NAME} />
    <Flex direction="column" minHeight="100vh">
      <ScrollToTop />
      <Navbar />
      <Box background={background} flex={1} pb={20}>
        <Warning />
        <GroupSelectionWarning />
        {children}
      </Box>
      <Footer />
    </Flex>
  </>
)
