import { Box, Button, ButtonGroup, Center, Heading, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import type { FC, PropsWithChildren } from 'react'
import { KirDevLogo } from '../assets/kir-dev-logo.tsx'
import { Loading } from '../common-components/Loading.tsx'
import { usePersistentStyleSetting } from './configs/themeStyle.config.ts'
import { useBrandColor } from './core-functions.util.ts'
import { l } from './language.ts'
import { Title } from './TitleProvider.tsx'

export type LoadingViewProps = PropsWithChildren & {
  hasError: boolean
  isLoading: boolean
  errorAction?: () => void
  errorTitle?: string
  errorMessage?: string
}

export const LoadingView: FC<LoadingViewProps> = ({ errorAction, hasError, errorTitle, errorMessage, isLoading, children }) => {
  const { persistentStyle: theme } = usePersistentStyleSetting()
  const backdropFilter = useColorModeValue(theme?.lightContainerFilter, theme?.darkContainerFilter)
  const bg = useColorModeValue('lightContainerBg', 'darkContainerBg')
  const brandColor = useBrandColor()

  if (hasError) {
    return (
      <Center flexDirection="column" h="100vh" backgroundPosition="center" backgroundSize="cover">
        <Title text={l('error-page-helmet')} />
        <VStack spacing={5} p={5} borderRadius={5} bg={bg} backdropFilter={backdropFilter}>
          <Heading textAlign="center">{errorTitle}</Heading>
          <Text textAlign="center" marginTop={4} maxW={96}>
            {errorMessage}
          </Text>
          <ButtonGroup justifyContent="center" marginTop={4}>
            <Button colorScheme={brandColor} onClick={errorAction}>
              Újra
            </Button>
          </ButtonGroup>
        </VStack>
      </Center>
    )
  }
  if (isLoading) {
    return (
      <Center flexDirection="column" h="100vh" backgroundPosition="center" backgroundSize="cover">
        <VStack p={5} borderRadius={5} bg={bg} backdropFilter={backdropFilter}>
          <Loading />
          <Box w={40} maxH={40} my={3}>
            <KirDevLogo />
          </Box>
        </VStack>
      </Center>
    )
  }

  return <>{children}</>
}
