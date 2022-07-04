import { GridItem, Heading, Image, Text } from '@chakra-ui/react'
import { Link, Navigate } from 'react-router-dom'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { EventListView } from '../../../util/views/event.view'
import EventTags from './EventTags'

interface EventListItemProps {
  event: EventListView
}

const EventListItem = ({ event }: EventListItemProps) => {
  const config = useConfigContext()

  if (typeof config === 'undefined') {
    return <Navigate replace to="/error" />
  }

  const isEnabled = config.components.event.enableDetailedView == 'true'
  const innerComponent = (
    <>
      <Heading mb={'0.5rem'}>{event.title}</Heading>
      <EventTags my={1} tags={[event.category, event.place]} />
      <Text mb="0.5rem">{stringifyTimeStamp(event.timestampStart) + ' - ' + stringifyTimeStamp(event.timestampEnd)}</Text>
      <Text>{event.previewDescription}</Text>
      <Image
        mt="1rem"
        display="block"
        ml="auto"
        mr="auto"
        src={event.previewImageUrl == '' ? 'https://picsum.photos/200' : event.previewImageUrl} //TODO random képet kivenni
        placeholder="ide kéne kép"
        h="10rem"
      />
    </>
  )

  return <GridItem>{isEnabled ? <Link to={'/esemenyek/' + event.url}> {innerComponent} </Link> : innerComponent}</GridItem>
}

export default EventListItem
