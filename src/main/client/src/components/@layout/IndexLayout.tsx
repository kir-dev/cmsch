import { Box, Flex } from '@chakra-ui/react'
import { Warning } from 'components/@commons/Warning'
import * as React from 'react'
import { Footer } from './Footer'
import { Navbar } from './navigation/Navbar'
import ScrollToTop from '../../utils/ScrollToTop'

type IndexLayoutProps = {
  background?: string
}

export const IndexLayout: React.FC<IndexLayoutProps> = ({ background, children }) => (
  <>
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
