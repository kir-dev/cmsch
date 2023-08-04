import { Box, Heading, Image, LinkBox, LinkOverlay, Text } from '@chakra-ui/react'
import { Link, Navigate } from 'react-router-dom'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { getCdnUrl, stringifyTimeRange } from '../../../util/core-functions.util'
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
    <Box as={LinkBox} w="100%" borderRadius="base" borderColor="whiteAlpha.200" borderWidth="1px" p={4}>
      <Heading fontSize={25} my={0}>
        {useLink ? (
          <LinkOverlay as={Link} to={`${AbsolutePaths.EVENTS}/${event.url}`}>
            {event.title}
          </LinkOverlay>
        ) : (
          event.title
        )}
      </Heading>
      <Text mb={2}>{stringifyTimeRange(event.timestampStart, event.timestampEnd)}</Text>
      {event.previewImageUrl && event.previewImageUrl !== '' && (
        <Image display="block" ml="auto" mr="auto" src={getCdnUrl(event.previewImageUrl)} maxH="8rem" />
      )}
      <Text my={2}>{event.previewDescription}</Text>
      <EventTags tags={[event.category, event.place]} />
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
