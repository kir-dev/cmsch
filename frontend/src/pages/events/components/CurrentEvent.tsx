import { ButtonGroup, Heading, Image, Text } from '@chakra-ui/react'
import { LinkButton } from '../../../common-components/LinkButton'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
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
      <CustomBreadcrumb items={breadcrumbItems} />
      <Heading>{event.title}</Heading>
      <Text>
        {stringifyTimeStamp(event.timestampStart)} &mdash; {stringifyTimeStamp(event.timestampEnd)}
      </Text>
      <EventTags my={1} tags={[event.category, event.place]} />
      {event.fullImageUrl && event.fullImageUrl !== '' && (
        <Image mb="1rem" display="block" ml="auto" mr="auto" src={event.fullImageUrl} h="20rem" />
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
