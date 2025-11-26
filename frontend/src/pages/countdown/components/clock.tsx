import { Flex, Heading, Icon, Text, VStack } from '@chakra-ui/react'
import { intervalToDuration } from 'date-fns'
import { useCallback, useEffect, useState } from 'react'
import { BsDashLg } from 'react-icons/bs'

interface ClockProps {
  countTo: Date
}

const Clock = ({ countTo }: ClockProps) => {
  // eslint-disable-next-line react-hooks/purity
  const target = countTo.getTime() > Date.now() ? countTo : new Date()
  const [duration, setDuration] = useState(intervalToDuration({ start: new Date(), end: target }))

  const update = useCallback(() => {
    if (countTo.getTime() > Date.now()) setDuration(intervalToDuration({ start: new Date(), end: countTo }))
  }, [countTo])

  useEffect(() => {
    const interval = setInterval(update, 1000)
    return () => clearInterval(interval)
  }, [update])

  return (
    <Flex flexDirection={['column', 'row']} alignItems="center" justifyContent="center">
      {(duration.years || 0) > 0 && (
        <>
          <ClockSegment value={duration.years?.toString()} label={'év'} />
          <Dash />
        </>
      )}
      {(duration.months || 0) > 0 && (
        <>
          <ClockSegment value={duration.months?.toString()} label={'hónap'} />
          <Dash />
        </>
      )}
      <ClockSegment value={duration.days?.toString()} label={'nap'} />
      <Dash />
      <ClockSegment value={duration.hours?.toString()} label={'óra'} />
      <Dash />
      <ClockSegment value={duration.minutes?.toString()} label={'perc'} />
      <Dash />
      <ClockSegment value={duration.seconds?.toString()} label={'másodperc'} />
    </Flex>
  )
}

interface ClockSegmentProps {
  value: string | undefined
  label: string
}

const ClockSegment = ({ value, label }: ClockSegmentProps) => {
  const valueText = (value || '00').padStart(2, '0')
  return (
    <VStack w="20%" mx={10} my={5}>
      <Heading fontSize={60} verticalAlign="center" lineHeight={10}>
        {valueText}
      </Heading>
      <Text>{label}</Text>
    </VStack>
  )
}

const Dash = () => <Icon size={30} as={BsDashLg} />

export default Clock
