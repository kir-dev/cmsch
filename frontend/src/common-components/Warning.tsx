import { Alert, AlertDescription, AlertIcon, CloseButton, HStack, useDisclosure, VStack } from '@chakra-ui/react'
import { CmschContainer } from './layout/CmschContainer'
import { useConfigContext } from '../api/contexts/config/ConfigContext'

export const Warning = () => {
  const { isOpen, onClose } = useDisclosure()
  const config = useConfigContext()

  if (!config || !isOpen) return null

  return (
    <CmschContainer>
      <Alert status={config.components.app.warningLevel || 'warning'} variant="left-accent">
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <VStack align="flex-start" flex={1}>
            <AlertDescription wordBreak="break-word">{config.components.app.warningMessage}</AlertDescription>
          </VStack>
          <CloseButton onClick={onClose} />
        </HStack>
      </Alert>
    </CmschContainer>
  )
}
