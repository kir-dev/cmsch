import { Alert, AlertDescription, AlertIcon, CloseButton, HStack, useDisclosure, VStack } from '@chakra-ui/react'
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
    <Alert status={data.type || 'warning'} variant="left-accent" mx="auto" w="100%" maxWidth={['100%', '48rem']} mb={5}>
      <HStack justify="space-between" flex={1}>
        <AlertIcon />
        <VStack align="flex-start" flex={1}>
          <AlertDescription wordBreak="break-word">{data.message}</AlertDescription>
        </VStack>
        <CloseButton onClick={onClose} />
      </HStack>
    </Alert>
  )
}
