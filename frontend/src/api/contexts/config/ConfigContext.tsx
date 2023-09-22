import { Box, Button, ButtonGroup, Center, Heading, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import React, { createContext, PropsWithChildren, useContext } from 'react'
import { Helmet } from 'react-helmet-async'
import { Loading } from '../../../common-components/Loading'
import { INITIAL_BG_IMAGE } from '../../../util/configs/environment.config'
import { l } from '../../../util/language'
import { useConfigQuery } from '../../hooks/config/useConfigQuery'
import { ConfigDto } from './types'
import { KirDevLogo } from '../../../assets/kir-dev-logo'

export const ConfigContext = createContext<ConfigDto | undefined>(undefined)

export const ConfigProvider = ({ children }: PropsWithChildren) => {
  const { data, isLoading, error, refetch } = useConfigQuery((err) =>
    console.error('[ERROR] at ConfigProvider', JSON.stringify(err, null, 2))
  )
  const bg = useColorModeValue('white', 'gray.900')
  if (isLoading)
    return (
      <Center flexDirection="column" h="100vh" backgroundImage={INITIAL_BG_IMAGE} backgroundPosition="center" backgroundSize="cover">
        <VStack p={5} borderRadius={5} bg={bg}>
          <Loading />
          <Box w={40} maxH={40} my={3}>
            <KirDevLogo />
          </Box>
        </VStack>
      </Center>
    )
  if (error)
    return (
      <Center flexDirection="column" h="100vh" backgroundImage={INITIAL_BG_IMAGE} backgroundPosition="center" backgroundSize="cover">
        <Helmet title={l('error-page-helmet')} />
        <VStack spacing={5} p={5} borderRadius={5} bg={bg}>
          <Heading textAlign="center">{l('error-page-title')}</Heading>
          <Text textAlign="center" color="gray.500" marginTop={10}>
            {l('error-connection-unsuccessful')}
          </Text>
          <ButtonGroup justifyContent="center" marginTop={10}>
            <Button
              colorScheme="brand"
              onClick={() => {
                refetch()
              }}
            >
              Újra
            </Button>
          </ButtonGroup>
        </VStack>
      </Center>
    )
  return <ConfigContext.Provider value={data}>{children}</ConfigContext.Provider>
}

export const useConfigContext = () => {
  const ctx = useContext(ConfigContext)
  if (typeof ctx === 'undefined') {
    throw new Error('useConfigContext must be used within a ConfigProvider')
  }
  return ctx
}
