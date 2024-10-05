import { Box, Flex, useColorModeValue } from '@chakra-ui/react'
import { useEventListQuery } from '../api/hooks/event/useEventListQuery'
import { isCurrentEvent, useOpaqueBackground } from '../util/core-functions.util'
import { AbsolutePaths } from '../util/paths'
import { Link } from 'react-router-dom'
import { PulsingDot } from './PulsingDot'

export default function CurrentEventCard() {
  const { data, error } = useEventListQuery()
  const color = useColorModeValue('brand.800', 'white')
  const background = useOpaqueBackground(1)
  if (!data || error) return null
  const currentEvents = data.filter((event) => isCurrentEvent(event))
  if (currentEvents.length === 0) return null

  function isVowel(x: any) {
    return /[aeiouAEIOU]/.test(x)
  }

  return (
    <Flex
      color={color}
      py={{ base: 2 }}
      px={{ base: 6 }}
      bg={background}
      textAlign={'left'}
      borderRadius={[0, null, 'xl']}
      m="2"
      direction={'row'}
      justify="left"
      alignItems="center"
    >
      <PulsingDot color={'success.300'} mr="4" />
      <Box position={'relative'} bottom="0px">
        {isVowel(currentEvents[0].title[0]) ? 'Az ' : 'A '}
        <b>
          {currentEvents.map((event, idx) => (
            <Link key={event.url} to={`${AbsolutePaths.EVENTS}/${event.url}`}>
              {event.title + (idx == currentEvents.length - 1 ? ' ' : ', ')}
            </Link>
          ))}
        </b>
        {currentEvents.length == 1 ? 'éppen most zajlik' : 'események éppen most zajlanak.'}
      </Box>
    </Flex>
  )
}
