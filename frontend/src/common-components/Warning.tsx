import { Alert, AlertDescription, AlertIcon, Box, CloseButton, HStack, useDisclosure, VStack } from '@chakra-ui/react'
import { useWarningQuery } from '../api/hooks/warning/useWarning'

export const Warning = () => {
  const { isOpen, onClose } = useDisclosure({ defaultIsOpen: true })
  const { data, error } = useWarningQuery()

  if (!data || error || !isOpen || !data.message) return null

  return (
    <Box>
      <Alert
        borderRadius={[0, null, 'xl']}
        opacity={1}
        status={data.type || 'warning'}
        variant="solid"
        mx="auto"
        w="100%"
        maxWidth={['100%', '64rem']}
        mb={5}
      >
        <HStack justify="space-between" flex={1}>
          <AlertIcon />
          <VStack align="flex-start" flex={1}>
            <AlertDescription wordBreak="break-word">{data.message}</AlertDescription>
          </VStack>
          <CloseButton onClick={onClose} />
        </HStack>
      </Alert>
    </Box>
  )
}
