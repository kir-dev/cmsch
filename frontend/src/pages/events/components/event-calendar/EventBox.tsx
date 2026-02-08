import {
  Box,
  Popover,
  PopoverArrow,
  PopoverBody,
  PopoverCloseButton,
  PopoverContent,
  PopoverFooter,
  PopoverHeader,
  PopoverTrigger,
  Text,
  useColorModeValue
} from '@chakra-ui/react'
import { RefObject } from 'react'
import { useConfigContext } from '../../../../api/contexts/config/ConfigContext'
import { LinkButton } from '../../../../common-components/LinkButton'
import { formatHu, stringifyTimeRange, useBrandColor } from '../../../../util/core-functions.util'
import { AbsolutePaths } from '../../../../util/paths'
import { EventListView } from '../../../../util/views/event.view'

export type EventBoxItem = EventListView & { top: number; bottom: number; width: number; left: number }

interface EventBoxProps {
  boxRef?: RefObject<HTMLDivElement | null>
  event: EventBoxItem
}

export function EventBox({ event, boxRef }: EventBoxProps) {
  const component = useConfigContext()?.components?.event
  const eventBg = useBrandColor(500, 300)
  const borderColor = useBrandColor(600, 600)
  const eventTextColor = useColorModeValue('white', 'black')
  const isShort = 100 - event.top - event.bottom < 5
  return (
    <Popover>
      <PopoverTrigger>
        <Box
          ref={boxRef}
          overflow="hidden"
          key={event.url}
          position="absolute"
          left={event.left + '%'}
          width={event.width + '%'}
          top={event.top + '%'}
          bottom={event.bottom + '%'}
          bg={eventBg}
          borderWidth={1}
          borderColor={borderColor}
          borderRadius="md"
          pt={isShort ? 0 : 1}
          pb={1}
          pl={1}
          color={eventTextColor}
        >
          <Text fontSize="sm" fontWeight="bold" whiteSpace="normal" wordBreak="break-all" lineHeight="1.1">
            {event.title}
          </Text>
          <Text opacity={0.5} whiteSpace="nowrap" overflow="hidden">
            {formatHu(event.timestampStart, 'HH:mm')} - {formatHu(event.timestampEnd, 'HH:mm')}
          </Text>
        </Box>
      </PopoverTrigger>
      <PopoverContent>
        <PopoverArrow />
        <PopoverCloseButton />
        <PopoverHeader isTruncated>{event.title}</PopoverHeader>
        <PopoverBody>{stringifyTimeRange(event.timestampStart, event.timestampEnd)}</PopoverBody>
        {component?.enableDetailedView && (
          <PopoverFooter>
            <LinkButton href={`${AbsolutePaths.EVENTS}/${event.url}`}>Részletek</LinkButton>
          </PopoverFooter>
        )}
      </PopoverContent>
    </Popover>
  )
}
