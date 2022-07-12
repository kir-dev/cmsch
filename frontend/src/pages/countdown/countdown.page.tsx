import { Helmet } from 'react-helmet'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { HasChildren } from '../../util/react-types.util'
import Clock from './components/clock'
import { Center, Flex, Heading, VStack } from '@chakra-ui/react'
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
  if (component?.enabled && (component?.informativeOnly === 'true' || countTo.getTime() > Date.now())) {
    return (
      <Flex h="100%" w="100%">
        <Helmet title={component?.title} />
        <Flex
          position="absolute"
          h="100%"
          w="100%"
          top={0}
          left={0}
          zIndex={0}
          backgroundPosition="center"
          backgroundSize="cover"
          backgroundImage={`url(${component.imageUrl})`}
          filter={component.blurredImage && 'blur(10px)'}
        />
        <Flex flexDirection="column" h="100%" w="100%" zIndex={1}>
          <Center h="100vh">
            <VStack w="100%">
              <Heading textAlign="center">{component.topMessage}</Heading>
              <Clock countTo={countTo} />
            </VStack>
          </Center>
        </Flex>
      </Flex>
    )
  } else return <>{children}</>
}

export default CountdownPage
