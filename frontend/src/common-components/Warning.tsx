import { Alert, AlertDescription, AlertIcon, Box, CloseButton, HStack, useDisclosure, VStack } from '@chakra-ui/react'
import { useConfigContext } from '../api/contexts/config/ConfigContext.tsx'

export const Warning = () => {
  const { isOpen, onClose } = useDisclosure({ defaultIsOpen: true })
  const config = useConfigContext()
  const message = config?.components?.app?.warningMessage
  const level = config?.components?.app?.warningLevel

  if (!isOpen || !message) return null

  return (
    <Box>
      <Alert
        borderRadius={[0, null, 'xl']}
        opacity={1}
        status={level || 'warning'}
        variant="solid"
        mx="auto"
        w="100%"
        maxWidth={['100%', '64rem']}
        mb={5}
      >
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <VStack align="flex-start" flex={1}>
            <AlertDescription wordBreak="break-word">{message}</AlertDescription>
          </VStack>
          <CloseButton onClick={onClose} />
        </HStack>
      </Alert>
    </Box>
  )
}
