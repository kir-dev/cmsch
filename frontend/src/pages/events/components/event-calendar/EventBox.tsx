import {
  Box,
  Popover,
  PopoverArrow,
  PopoverBody,
  PopoverContent,
  PopoverFooter,
  PopoverHeader,
  PopoverTrigger,
  Text
} from '@chakra-ui/react'
import { RefObject } from 'react'
import { useConfigContext } from '../../../../api/contexts/config/ConfigContext'
import { LinkButton } from '../../../../common-components/LinkButton'
import { formatHu, stringifyTimeRange } from '../../../../util/core-functions.util'
import { AbsolutePaths } from '../../../../util/paths'
import { EventListView } from '../../../../util/views/event.view'
import { useColorModeValue } from '../../../../components/ui/color-mode.tsx'
import { PopoverCloseTrigger } from '../../../../components/ui/popover.tsx'

export type EventBoxItem = EventListView & { top: number; bottom: number; conflictingEventsBefore?: number }

interface EventBoxProps {
  boxRef?: RefObject<HTMLDivElement | null>
  event: EventBoxItem
}

export function EventBox({ event, boxRef }: EventBoxProps) {
  const component = useConfigContext()?.components?.event
  const eventBg = useColorModeValue('brand.500', 'brand.300')
  const eventTextColor = useColorModeValue('white', 'black')
  return (
    <Popover.Root>
      <PopoverTrigger>
        <Box
          ml={(event.conflictingEventsBefore ?? 0) * 2}
          ref={boxRef}
          overflow="hidden"
          key={event.url}
          position="absolute"
          left={0}
          right={0}
          top={event.top + '%'}
          bottom={event.bottom + '%'}
          bg={eventBg}
          borderWidth={1}
          borderColor="brand.600"
          borderRadius="md"
          p={1}
          color={eventTextColor}
        >
          <Text fontSize="sm" fontWeight="bold" truncate>
            {event.title}
          </Text>
          <Text opacity={0.5}>
            {formatHu(event.timestampStart, 'HH:mm')} - {formatHu(event.timestampEnd, 'HH:mm')}
          </Text>
        </Box>
      </PopoverTrigger>
      <PopoverContent>
        <PopoverArrow />
        <PopoverCloseTrigger />
        <PopoverHeader truncate>{event.title}</PopoverHeader>
        <PopoverBody>{stringifyTimeRange(event.timestampStart, event.timestampEnd)}</PopoverBody>
        {component.enableDetailedView && (
          <PopoverFooter>
            <LinkButton href={`${AbsolutePaths.EVENTS}/${event.url}`}>Részletek</LinkButton>
          </PopoverFooter>
        )}
      </PopoverContent>
    </Popover.Root>
  )
}
