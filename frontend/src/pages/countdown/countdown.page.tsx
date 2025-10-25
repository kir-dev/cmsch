import { Center, Flex, Heading, VStack } from '@chakra-ui/react'
import { PropsWithChildren, useMemo } from 'react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import Clock from './components/clock'
import { parseTopMessage } from './countdown.util'

const CountdownPage = ({ children }: PropsWithChildren) => {
  const config = useConfigContext()

  const component = config?.components?.countdown
  const countTo = useMemo(() => {
    try {
      if (!component) return new Date()
      return new Date(component?.timeToCountTo * 1000)
    } catch (e) {
      console.error(e)
      return new Date()
    }
  }, [component])
  if (component?.enabled && component?.showOnly && (component?.keepOnAfterCountdownOver || countTo.getTime() > Date.now())) {
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
          filter={component.blurredImage ? 'blur(15px)' : undefined}
        />
        <Flex flexDirection="column" h="100%" w="100%" zIndex={1} overflow="auto">
          <Center h="100vh">
            <VStack w="100%" maxH="100%">
              <Heading textAlign="center">{parseTopMessage(component.topMessage)}</Heading>
              {component.showRemainingTime && <Clock countTo={countTo} />}
            </VStack>
          </Center>
        </Flex>
      </Flex>
    )
  } else return <>{children}</>
}

export default CountdownPage
