import { Alert, AlertDescription, AlertIcon, CloseButton, HStack, useDisclosure, VStack } from '@chakra-ui/react'
import { CmschContainer } from './layout/CmschContainer'
import { useWarningQuery } from '../api/hooks/commons/useWarningQuery'
import { useEffect } from 'react'

export const Warning = () => {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const { isLoading, error, data: warning } = useWarningQuery()
  useEffect(() => {
    if (warning?.message !== '') {
      onOpen()
    }
  }, [warning])

  if (error) {
    console.log('[ERROR] at useWarningQuery', error, (error as any)?.response.data.message)
  }

  if (warning === undefined || !isOpen) return null

  return (
    <CmschContainer>
      <Alert status={warning.type || 'warning'} variant="left-accent">
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <VStack align="flex-start" flex={1}>
            <AlertDescription wordBreak="break-word">{warning.message}</AlertDescription>
          </VStack>
          <CloseButton onClick={onClose} />
        </HStack>
      </Alert>
    </CmschContainer>
  )
}
