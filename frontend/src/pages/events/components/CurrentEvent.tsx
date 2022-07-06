import { Heading, Image, Text } from '@chakra-ui/react'
import { LinkButton } from '../../../common-components/LinkButton'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { EventView } from '../../../util/views/event.view'
import EventTags from './EventTags'
import { AbsolutePaths } from '../../../util/paths'

interface EventProps {
  event: EventView
}

const CurrentEvent = ({ event }: EventProps) => {
  return (
    <>
      <Heading>{event.title}</Heading>
      <Text>{stringifyTimeStamp(event.timestampStart) + '-' + stringifyTimeStamp(event.timestampEnd)}</Text>
      <EventTags my={1} tags={[event.category, event.place]} />
      <Image
        mb="1rem"
        display="block"
        ml="auto"
        mr="auto"
        src={event.fullImageUrl == '' ? 'https://picsum.photos/200' : event.fullImageUrl} //TODO random képet kivenni
        placeholder="ide kéne kép"
        h="20rem"
      />
      <Markdown text={event.description} />
      {event.extraButtonUrl && (
        <LinkButton w="13rem" mt="1rem" href={event.extraButtonUrl} external>
          {event.extraButtonTitle}
        </LinkButton>
      )}
      <LinkButton w="13rem" mt="2rem" href={AbsolutePaths.EVENTS}>
        Vissza az eseményekhez
      </LinkButton>
    </>
  )
}

export default CurrentEvent
