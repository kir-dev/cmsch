import { Box, Flex } from '@chakra-ui/react'
import * as React from 'react'
import { Footer } from './Footer'
import { Navbar } from './navigation/Navbar'

type IndexLayoutProps = {
  background?: string
}

export const IndexLayout: React.FC<IndexLayoutProps> = ({ background, children }) => (
  <>
    <Flex direction="column" minHeight="100vh">
      <Navbar />
      <Box background={background} flex={1} pb={20}>
        {children}
      </Box>
      <Footer />
    </Flex>
  </>
)
