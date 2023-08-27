import { Box, Heading, LinkBox, LinkOverlay, Text, useColorModeValue } from '@chakra-ui/react'
import { Link, Navigate } from 'react-router-dom'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { EventIndicator } from '../../../common-components/EventIndicator'
import { getCdnUrl, isCurrentEvent, isUpcomingEvent, stringifyTimeRange } from '../../../util/core-functions.util'
import { AbsolutePaths } from '../../../util/paths'
import { EventListView } from '../../../util/views/event.view'
import EventTags from './EventTags'

interface EventListItemProps {
  event: EventListView
  useLink?: boolean
}

const EventListItem = ({ event, useLink }: EventListItemProps) => {
  const config = useConfigContext()

  if (typeof config === 'undefined') {
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
  const innerComponent = (
    <Box overflow="hidden" position="relative" as={LinkBox} w="100%" borderRadius="base" borderColor="whiteAlpha.200" borderWidth="1px">
      <Box
        backgroundImage={event.previewImageUrl ? getCdnUrl(event.previewImageUrl) : undefined}
        backgroundPosition="center"
        backgroundSize="cover"
      >
        <Box p={4} bg={event.previewImageUrl ? useColorModeValue('#FFFFFFAA', '#00000080') : undefined}>
          <Heading fontSize={25} my={0}>
            {useLink ? (
              <LinkOverlay as={Link} to={`${AbsolutePaths.EVENTS}/${event.url}`}>
                {event.title}
              </LinkOverlay>
            ) : (
              event.title
            )}
          </Heading>
          <Text>{stringifyTimeRange(event.timestampStart * 1000, event.timestampEnd * 1000)}</Text>
        </Box>
      </Box>
      <Box p={4}>
        <Text mb={4}>{event.previewDescription}</Text>
        <EventTags tags={[event.category, event.place]} />
      </Box>
      <EventIndicator position="absolute" top={4} right={4} isCurrent={isCurrentEvent(event)} isUpcoming={isUpcomingEvent(event)} />
    </Box>
  )

  return (
    <Box w="100%">
      {config.components.event?.enableDetailedView ? (
        <Link to={`${AbsolutePaths.EVENTS}/${event.url}`}> {innerComponent} </Link>
      ) : (
        innerComponent
      )}
    </Box>
  )
}

export default EventListItem
