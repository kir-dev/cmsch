import { CmschPage } from '../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { HasChildren } from '../../util/react-types.util'
import Clock from './components/clock'
import { Center, Heading, VStack } from '@chakra-ui/react'
import { useMemo } from 'react'

const CountdownPage = ({ children }: HasChildren) => {
  const config = useConfigContext()
  const component = config?.components?.countdown
  const countTo = useMemo(() => {
    try {
      if (!component) return new Date()
      return new Date(parseInt(component?.timeToCountTo) * 1000)
    } catch (e) {
      return new Date()
    }
  }, [component])
  if (component?.enabled === 'false' && (component?.informativeOnly === 'true' || countTo.getTime() > Date.now())) {
    return (
      <CmschPage>
        <Helmet title={component?.title} />
        <Center h="100vh">
          <VStack w="100%">
            <Heading textAlign="center">{component.topMessage}</Heading>
            <Clock countTo={countTo} />
          </VStack>
        </Center>
      </CmschPage>
    )
  } else return <>{children}</>
}

export default CountdownPage
