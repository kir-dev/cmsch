import { Center, Spinner, useColorModeValue } from '@chakra-ui/react'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'

export function LoadingPage() {
  const color = useColorModeValue('brand.500', 'brand.600')

  return (
    <Loading>
      <CmschPage title="Betöltés" w="fit-content">
        <Center>
          <Spinner color={color} size="xl" thickness="0.3rem" />
        </Center>
      </CmschPage>
    </Loading>
  )
}
