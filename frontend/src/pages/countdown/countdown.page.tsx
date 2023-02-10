import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import Clock from './components/clock'
import { Center, Flex, Heading, useColorModeValue, VStack } from '@chakra-ui/react'
import { PropsWithChildren, useMemo } from 'react'
import { parseTopMessage } from './countdown.util'

const CountdownPage = ({ children }: PropsWithChildren) => {
  const config = useConfigContext()
  const component = config?.components?.countdown
  const countTo = useMemo(() => {
    try {
      if (!component) return new Date()
      return new Date(component?.timeToCountTo * 1000)
    } catch (e) {
      return new Date()
    }
  }, [component])
  if (component?.enabled && component?.showOnly && (component?.informativeOnly || countTo.getTime() > Date.now())) {
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
        <Flex flexDirection="column" h="100%" w="100%" zIndex={1} backgroundColor={useColorModeValue('#FFFFFFAA', '#000000AA')}>
          <Center h="100vh">
            <VStack w="100%" maxH="100%" overflow="auto" color={useColorModeValue('#000000', '#FFFFFF')}>
              <Heading textAlign="center">{parseTopMessage(component.topMessage)}</Heading>
              <Clock countTo={countTo} />
            </VStack>
          </Center>
        </Flex>
      </Flex>
    )
  } else return <>{children}</>
}

export default CountdownPage
