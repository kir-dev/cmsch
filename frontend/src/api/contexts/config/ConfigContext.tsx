import { createContext, useContext } from 'react'
import { HasChildren } from '../../../util/react-types.util'
import { ConfigDto } from './types'
import { useConfigQuery } from '../../hooks/useConfigQuery'
import { Loading } from '../../../common-components/Loading'
import { Button, ButtonGroup, Heading, Text } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { CmschPage } from '../../../common-components/layout/CmschPage'
import { l } from '../../../util/language'

export const ConfigContext = createContext<ConfigDto | undefined>(undefined)

export const ConfigProvider = ({ children }: HasChildren) => {
  const { data, isLoading, error, refetch } = useConfigQuery((err) =>
    console.error('[ERROR] at ConfigProvider', JSON.stringify(err, null, 2))
  )
  if (isLoading) return <Loading />
  if (error)
    return (
      <CmschPage>
        <Helmet title={l('error-page-helmet')} />
        <Heading textAlign="center">l("error-page-title")</Heading>
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
      </CmschPage>
    )
  return <ConfigContext.Provider value={data}>{children}</ConfigContext.Provider>
}

export const useConfigContext = () => useContext<ConfigDto | undefined>(ConfigContext)
