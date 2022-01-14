import { ChakraProvider, Code, Grid, Link, Text, VStack } from '@chakra-ui/react'
import * as React from 'react'

import customTheme from './utils/customTheme'
import { IndexLayout } from './components/@layout/IndexLayout'
import { ExampleApiCallComponent } from './components/@commons/ExampleApiCallComponent'

export function App() {
  return (
    <ChakraProvider theme={customTheme}>
      <IndexLayout>
        <Grid minH="50vh" p={3}>
          <VStack spacing={8}>
            <Text>
              Edit <Code fontSize="xl">src/App.tsx</Code> and save to reload.
            </Text>
            <Link color="teal.500" href="https://chakra-ui.com" fontSize="2xl" target="_blank" rel="noopener noreferrer">
              Learn Chakra
            </Link>
            <ExampleApiCallComponent />
          </VStack>
        </Grid>
      </IndexLayout>
    </ChakraProvider>
  )
}
