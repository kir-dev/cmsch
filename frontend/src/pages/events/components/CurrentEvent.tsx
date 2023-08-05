import { ButtonGroup, Heading, Image, Text } from '@chakra-ui/react'
import { EventIndicator } from '../../../common-components/EventIndicator'
import { LinkButton } from '../../../common-components/LinkButton'
import Markdown from '../../../common-components/Markdown'
import {
  DETAILED_TIMESTAMP_OPTIONS,
  getCdnUrl,
  isCurrentEvent,
  isUpcomingEvent,
  stringifyTimeStamp
} from '../../../util/core-functions.util'
import { EventView } from '../../../util/views/event.view'
import EventTags from './EventTags'
import { AbsolutePaths } from '../../../util/paths'
import { CustomBreadcrumb } from '../../../common-components/CustomBreadcrumb'

interface EventProps {
  event: EventView
}

const CurrentEvent = ({ event }: EventProps) => {
  const breadcrumbItems = [
    {
      title: 'Események',
      to: AbsolutePaths.EVENTS
    },
    {
      title: event.title
    }
  ]
  return (
    <>
      <EventIndicator
        showLabel
        position="absolute"
        top={5}
        right={5}
        isCurrent={isCurrentEvent(event)}
        isUpcoming={isUpcomingEvent(event)}
      />
      <CustomBreadcrumb items={breadcrumbItems} />
      <Heading>{event.title}</Heading>
      <Text>
        {stringifyTimeStamp(event.timestampStart, DETAILED_TIMESTAMP_OPTIONS)} &mdash;{' '}
        {stringifyTimeStamp(event.timestampEnd, DETAILED_TIMESTAMP_OPTIONS)}
      </Text>
      <EventTags my={1} tags={[event.category, event.place]} />
      {event.fullImageUrl && event.fullImageUrl !== '' && (
        <Image mb="1rem" display="block" ml="auto" mr="auto" src={getCdnUrl(event.fullImageUrl)} maxH="20rem" />
      )}
      <Markdown text={event.description} />
      <ButtonGroup justifyContent="space-between" mt={10}>
        <LinkButton href={AbsolutePaths.EVENTS} variant="outline">
          Vissza
        </LinkButton>
        {event.extraButtonUrl && (
          <LinkButton href={event.extraButtonUrl} external>
            {event.extraButtonTitle}
          </LinkButton>
        )}
      </ButtonGroup>
    </>
  )
}

export default CurrentEvent
