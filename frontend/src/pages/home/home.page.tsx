import { Box, Divider, Heading, Text, VStack } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { useMemo } from 'react'
import { Helmet } from 'react-helmet-async'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { AbsolutePaths } from '../../util/paths'
import Clock from '../countdown/components/clock'
import { Schedule } from './components/Schedule'
import { useEventListQuery } from '../../api/hooks/event/useEventListQuery'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { EmbeddedVideo } from './components/EmbeddedVideo'

const HomePage = () => {
  const eventList = useEventListQuery(() => console.log('Event list query failed!'))
  const config = useConfigContext()

  const countTo = useMemo(() => {
    const component = config?.components.countdown
    try {
      if (!component) return new Date()
      return new Date(component?.timeToCountTo * 1000)
    } catch (e) {
      return new Date()
    }
  }, [config?.components.countdown])

  const events = useMemo(() => {
    const timestampCorrectedEventList = eventList.data?.map((li) => {
      const { timestampStart, timestampEnd, ...rest } = li
      return { timestampStart: timestampStart * 1000, timestampEnd: timestampEnd * 1000, ...rest }
    })
    return timestampCorrectedEventList?.filter((li) => li.timestampStart > Date.now()).sort((a, b) => a.timestampStart - b.timestampStart)
  }, [eventList.data])

  const eventsToday = events?.filter((ev) => isToday(ev.timestampStart)) || []
  const eventsLater = events?.filter((ev) => !isToday(ev.timestampStart)).slice(0, 3) || []

  return (
    <CmschPage>
      <Helmet />
      {config?.components.home?.welcomeMessage && (
        <Heading size="3xl" textAlign="center" marginTop={10}>
          {config?.components.home.welcomeMessage.split('{}')[0] + ' '}
          {config?.components.home.welcomeMessage.split('{}').length > 1 && (
            <>
              <Heading as="span" color={useColorModeValue('brand.500', 'brand.500')} size="3xl">
                {config?.components.app.siteName || 'CMSch'}
              </Heading>{' '}
              {config?.components.home.welcomeMessage.split('{}')[1]}
            </>
          )}
        </Heading>
      )}

      {config?.components.countdown?.enabled && (
        <>
          <Heading textAlign="center">{config?.components.countdown?.topMessage}</Heading>
          <Clock countTo={countTo} />
        </>
      )}

      {config?.components.home?.youtubeVideoId && (
        <EmbeddedVideo key={config?.components.home?.youtubeVideoId} id={config?.components.home?.youtubeVideoId} />
      )}

      {config?.components.home?.content && (
        <Box mt={10}>
          <Markdown text={config?.components.home?.content} />
        </Box>
      )}

      {eventList.data && config?.components.home?.showEvents && (
        <VStack>
          <Heading as="h2" size="lg" textAlign="center" mb={5} mt={20}>
            {config?.components.event.title}
          </Heading>
          <VStack spacing={10}>
            <Text textAlign="center" fontSize={25} fontWeight="bolder" marginTop={10}>
              Mai nap
            </Text>
            {eventsToday.length > 0 ? (
              <Schedule events={eventsToday} />
            ) : (
              <Text textAlign="center" color="gray.500" marginTop={10}>
                Nincs több esemény.
              </Text>
            )}
            <Divider />
            <Text textAlign="center" fontSize={25} fontWeight="bolder" marginTop={10}>
              Később
            </Text>
            {eventsLater.length > 0 ? (
              <Schedule verbose events={eventsLater} />
            ) : (
              <Text textAlign="center" color="gray.500" marginTop={10}>
                Nincs több esemény.
              </Text>
            )}
            <LinkButton href={AbsolutePaths.EVENTS}>Részletek</LinkButton>
          </VStack>
        </VStack>
      )}
    </CmschPage>
  )
}

export default HomePage

const isToday = (timeStamp: number) => new Date(timeStamp).toDateString() === new Date().toDateString()
