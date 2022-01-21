import { Page } from '../@layout/Page'
import { Box, Heading, Text, Divider, SpaceProps, HStack, Tag } from '@chakra-ui/react'
import axios from 'axios'
import React from 'react'
import { EventsPreviewDTO, EventsView } from 'types/dto/events'
import { API_BASE_URL } from 'utils/configurations'
import { Paragraph } from 'components/@commons/Basics'

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

  const getDate = (d: number) => {
    return new Date(d * 1000).toLocaleString('hu', { month: 'short', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  }

  interface IEventTags {
    tags: Array<string>
    marginTop?: SpaceProps['marginTop']
  }

  const EventTags: React.FC<IEventTags> = (props) => {
    return (
      <HStack spacing={2} marginTop={props.marginTop}>
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

  return (
    <Page {...props}>
      <Heading>Események</Heading>
      {eventsList?.allEvents.map((item: EventsPreviewDTO) => (
        <Box key={item.title}>
          <Divider />
          <Box marginY="6" display="flex" flex="1" flexDirection="column" justifyContent="center">
            <Heading>{item.title}</Heading>
            <EventTags marginTop={0} tags={[item.category, item.place]} />
            <Text>
              {getDate(item.timestampStart)} — {getDate(item.timestampEnd)}
            </Text>
            <Paragraph>{item.previewDescription}</Paragraph>
          </Box>
        </Box>
      ))}
    </Page>
  )
}
