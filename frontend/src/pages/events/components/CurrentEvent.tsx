import { Button, Heading, Image, Text } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { CmschLink } from '../../../common-components/CmschLink'
import Markdown from '../../../common-components/Markdown'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { EventView } from '../../../util/views/event.view'
import EventTags from './EventTags'

interface EventProps {
  event: EventView
}

const CurrentEvent = ({ event }: EventProps) => {
  console.log('ITT KEZDŐDIK-----------------')
  console.log(event)
  console.log(event.description)
  console.log('DAKJHLHJKLDADAHHIOLADOLIHJADOLI')
  return (
    <>
      <Heading>{event.title}</Heading>
      <Text>{stringifyTimeStamp(event.timestampStart) + '-' + stringifyTimeStamp(event.timestampEnd)}</Text>
      <EventTags my={1} tags={[event.category, event.place]} />
      <Image
        display="block"
        ml="auto"
        mr="auto"
        src={event.fullImageUrl == '' ? 'https://picsum.photos/200' : event.fullImageUrl} //TODO random képet kivenni
        placeholder="ide kéne kép"
        h="20rem"
      />
      <Markdown text={event.description} />
      {event.extraButtonUrl && (
        <Link to={event.extraButtonUrl}>
          <Button>{event.extraButtonTitle}</Button>
        </Link>
      )}
      <Link to="/esemenyek">
        <Button mt="2rem">Vissza az eseményekhez</Button>
      </Link>
    </>
  )
}

export default CurrentEvent
