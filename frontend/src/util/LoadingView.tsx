import { FC, PropsWithChildren } from 'react'
import { Box, Button, ButtonGroup, Center, Heading, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { INITIAL_BG_IMAGE } from './configs/environment.config.ts'
import { Loading } from '../common-components/Loading.tsx'
import { KirDevLogo } from '../assets/kir-dev-logo.tsx'
import { Helmet } from 'react-helmet-async'
import { l } from './language.ts'

export type LoadingViewProps = PropsWithChildren & {
  hasError: boolean
  isLoading: boolean
  errorAction?: () => void
  errorTitle?: string
  errorMessage?: string
}

export const LoadingView: FC<LoadingViewProps> = ({ errorAction, hasError, errorTitle, errorMessage, isLoading, children }) => {
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
  if (hasError) {
    return (
      <Center flexDirection="column" h="100vh" backgroundImage={INITIAL_BG_IMAGE} backgroundPosition="center" backgroundSize="cover">
        <Helmet title={l('error-page-helmet')} />
        <VStack spacing={5} p={5} borderRadius={5} bg={bg}>
          <Heading textAlign="center">{errorTitle}</Heading>
          <Text textAlign="center" color="gray.500" marginTop={4} maxW={96}>
            {errorMessage}
          </Text>
          <ButtonGroup justifyContent="center" marginTop={4}>
            <Button colorScheme="brand" onClick={errorAction}>
              Újra
            </Button>
          </ButtonGroup>
        </VStack>
      </Center>
    )
  }

  return <>{children}</>
}
