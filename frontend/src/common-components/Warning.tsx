import { Alert, AlertDescription, AlertIcon, CloseButton, HStack, useDisclosure, VStack } from '@chakra-ui/react'
import { CmschContainer } from './layout/CmschContainer'
import { useWarningQuery } from '../api/hooks/useWarning'
import { useEffect } from 'react'

export const Warning = () => {
  const { isOpen, onClose } = useDisclosure({ defaultIsOpen: true })
  const { data, error, refetch } = useWarningQuery()

  useEffect(() => {
    window.onfocus = () => {
      refetch()
    }
  }, [])

  if (!data || error || !isOpen || !data.message) return null

  return (
    <CmschContainer>
      <Alert status={data.type || 'warning'} variant="left-accent">
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <VStack align="flex-start" flex={1}>
            <AlertDescription wordBreak="break-word">{data.message}</AlertDescription>
          </VStack>
          <CloseButton onClick={onClose} />
        </HStack>
      </Alert>
    </CmschContainer>
  )
}
