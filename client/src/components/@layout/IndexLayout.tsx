import { Box, Flex } from '@chakra-ui/react'
import { Warning } from 'components/@commons/Warning'
import { FC } from 'react'
import { Helmet } from 'react-helmet'
import { APP_NAME } from 'utils/configurations'
import ScrollToTop from '../../utils/ScrollToTop'
import { Footer } from './Footer'
import { Navbar } from './navigation/Navbar'

type IndexLayoutProps = {
  background?: string
}

export const IndexLayout: FC<IndexLayoutProps> = ({ background, children }) => (
  <>
    <Helmet titleTemplate={`%s | ${APP_NAME}`} defaultTitle={APP_NAME} />
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
