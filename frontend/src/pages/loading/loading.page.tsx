import { Center, Spinner } from '@chakra-ui/react'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Loading } from '../../common-components/Loading'
import { useBrandColor } from '../../util/core-functions.util.ts'

export function LoadingPage() {
  const color = useBrandColor(500, 600)

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
