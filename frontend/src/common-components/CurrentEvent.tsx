import { Alert, AlertDescription, AlertIcon, HStack } from '@chakra-ui/react'
import { useEventListQuery } from '../api/hooks/event/useEventListQuery'
import { isCurrentEvent } from '../util/core-functions.util'

export default function CurrentEventCard() {
  const { data, error } = useEventListQuery()
  if (!data || error) return null
  const currentEvents = data.filter((event) => isCurrentEvent(event))
  if (currentEvents.length === 0) return null
  return (
    <Alert
      borderRadius={[0, null, 'xl']}
      opacity={1}
      status={'info'}
      variant="solid"
      mx="auto"
      w="100%"
      maxWidth={['100%', '64rem']}
      mb={5}
    >
      <HStack justify="left" flex={1}>
        <AlertIcon />
        <AlertDescription wordBreak="break-word">
          <p>
            A <b>{currentEvents.map((event) => event.title).join(', ')}</b>
            {currentEvents.length == 1 ? ' esemény éppen most zajlik' : ' események éppen most zajlanak.'}
          </p>
        </AlertDescription>
      </HStack>
    </Alert>
  )
}
