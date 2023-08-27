import React, { createContext, PropsWithChildren, useContext } from 'react'
import { Helmet } from 'react-helmet-async'
import { ConfigDto } from './types'
import { useConfigQuery } from '../../hooks/config/useConfigQuery'
import { Loading } from '../../../common-components/Loading'
import { Button, ButtonGroup, Center, Heading, Image, Text, useColorModeValue } from '@chakra-ui/react'
import { l } from '../../../util/language'

export const ConfigContext = createContext<ConfigDto | undefined>(undefined)

export const ConfigProvider = ({ children }: PropsWithChildren) => {
  const { data, isLoading, error, refetch } = useConfigQuery((err) =>
    console.error('[ERROR] at ConfigProvider', JSON.stringify(err, null, 2))
  )
  const kirDevLogo = useColorModeValue('/img/kirdev.svg', '/img/kirdev-white.svg')
  if (isLoading)
    return (
      <Center flexDirection="column" h="100vh">
        <Loading />
        <Image src={kirDevLogo} maxW={40} maxH={40} my={3} />
      </Center>
    )
  if (error)
    return (
      <Center flexDirection="column" h="100vh">
        <Helmet title={l('error-page-helmet')} />
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
