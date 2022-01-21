import { Page } from '../@layout/Page'
import { Box, Heading, Text, Divider, SpaceProps, HStack, Tag } from '@chakra-ui/react'
import axios from 'axios'
import React from 'react'
import { EventsPreviewDTO, EventsView } from 'types/dto/events'
import { API_BASE_URL } from 'utils/configurations'
import { Paragraph } from 'components/@commons/Basics'
import { stringifyTimeStamp } from 'utils/utilFunctions'

interface IEventTags {
  tags: Array<string>
  my?: SpaceProps['my']
}

const EventTags: React.FC<IEventTags> = (props) => {
  return (
    <HStack spacing={2} my={props.my}>
      {props.tags.map((tag) => {
        return (
          <Tag size={'md'} variant="solid" colorScheme="brand" key={tag}>
            {tag}
          </Tag>
        )
      })}
    </HStack>
  )
}

type EventPageProps = {}

export const EventPage: React.FC<EventPageProps> = (props) => {
  const [eventsList, setEventsList] = React.useState<EventsView>({
    warningMessage: '',
    allEvents: []
  })

  React.useEffect(() => {
    axios.get<EventsView>(`${API_BASE_URL}/api/events`).then((res) => {
      if (res.status !== 200) {
        console.log(res)
        return
      }
      setEventsList(res.data)
    })
  }, [setEventsList])

  return (
    <Page {...props}>
      <Heading>Események</Heading>
      {eventsList?.allEvents.map((item: EventsPreviewDTO) => (
        <Box key={item.title}>
          <Divider />
          <Box marginY="6" display="flex" flex="1" flexDirection="column" justifyContent="center">
            <Heading>{item.title}</Heading>
            <EventTags my={1} tags={[item.category, item.place]} />
            <Text>
              {stringifyTimeStamp(item.timestampStart)} — {stringifyTimeStamp(item.timestampEnd)}
            </Text>
            <Paragraph>{item.previewDescription}</Paragraph>
          </Box>
        </Box>
      ))}
    </Page>
  )
}
