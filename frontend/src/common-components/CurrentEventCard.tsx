import { Alert, AlertDescription, AlertIcon, Box, Flex, HStack, useColorMode, useColorModeValue } from '@chakra-ui/react'
import { useEventListQuery } from '../api/hooks/event/useEventListQuery'
import { isCurrentEvent } from '../util/core-functions.util'
import { AbsolutePaths } from '../util/paths'
import { Link } from 'react-router-dom'
import { CmschContainer } from './layout/CmschContainer'
import { useConfigContext } from '../api/contexts/config/ConfigContext'
import { useOpaqueBackground } from '../util/core-functions.util'
import { PulsingDot } from './PulsingDot'
import { relative } from 'path'

export default function CurrentEventCard() {
  const config = useConfigContext()
  const { data, error } = useEventListQuery()
  if (!data || error) return null
  const currentEvents = data.filter((event) => isCurrentEvent(event))
  if (currentEvents.length === 0) return null
  function isVowel(x: any) {
    return /[aeiouAEIOU]/.test(x)
  }
  return (
    <Flex
      color={useColorModeValue('brand.800', 'white')}
      py={{ base: 2 }}
      px={{ base: 6 }}
      bg={useOpaqueBackground(1)}
      textAlign={'left'}
      borderRadius={[0, null, 'xl']}
      m="2"
      direction={'row'}
      justify="left"
      alignItems="center"
    >
      <PulsingDot color={'green.300'} mr="4" />
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
