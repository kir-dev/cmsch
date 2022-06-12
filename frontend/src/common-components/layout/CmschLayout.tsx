import { Box, Flex } from '@chakra-ui/react'
import * as React from 'react'
import { Helmet } from 'react-helmet'
import { Footer } from '../footer/Footer'
import { HasChildren } from '../../util/react-types.util'
import { APP_NAME } from '../../util/configs/environment.config'
import { Navbar } from '../navigation/Navbar'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'

type Props = {
  background?: string
} & HasChildren

export const CmschLayout = ({ background, children }: Props) => (
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
