import { Divider, Heading, Text, VStack } from '@chakra-ui/react'
import { useMemo } from 'react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext.tsx'
import { useEventListQuery } from '../../../api/hooks/event/useEventListQuery.ts'
import { LinkButton } from '../../../common-components/LinkButton.tsx'
import { useBrandColor } from '../../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../../util/paths.ts'
import { Schedule } from './Schedule.tsx'

const isToday = (timeStamp: number) => new Date(timeStamp).toDateString() === new Date().toDateString()

export default function HomePageEventList() {
  const config = useConfigContext()
  const eventList = useEventListQuery()
  const brandColor = useBrandColor()

  const events = useMemo(() => {
    const timestampCorrectedEventList = eventList.data?.map((li) => {
      const { timestampStart, timestampEnd, ...rest } = li
      return { timestampStart: timestampStart * 1000, timestampEnd: timestampEnd * 1000, ...rest }
    })
    return timestampCorrectedEventList?.filter((li) => li.timestampStart > Date.now()).sort((a, b) => a.timestampStart - b.timestampStart)
  }, [eventList.data])

  const eventsToday = events?.filter((ev) => isToday(ev.timestampStart)) || []
  const eventsLater = events?.filter((ev) => !isToday(ev.timestampStart)).slice(0, 3) || []

  if (!eventList.data) return null
  return (
    <VStack>
      <Heading as="h2" size="lg" textAlign="center" mb={5} mt={20}>
        {config?.components?.event?.title}
      </Heading>
      <VStack spacing={10}>
        <Text textAlign="center" fontSize={25} fontWeight="bolder" marginTop={10}>
          Mai nap
        </Text>
        {eventsToday.length > 0 ? (
          <Schedule events={eventsToday} />
        ) : (
          <Text textAlign="center" opacity={0.7} marginTop={10}>
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
          <Text textAlign="center" opacity={0.7} marginTop={10}>
            Nincs több esemény.
          </Text>
        )}
        <LinkButton colorScheme={brandColor} href={AbsolutePaths.EVENTS}>
          Részletek
        </LinkButton>
      </VStack>
    </VStack>
  )
}
