import { Box, StackProps, Text } from '@chakra-ui/react'
import { addHours, format } from 'date-fns'
import { useMemo } from 'react'
import { calculatePosition } from './event-calendar/utils'

export function HourColumn({ mt, h, ...props }: StackProps) {
  const dates = useMemo(() => {
    const datesTemp: Date[] = []
    for (let i = 0; i < 25; i++) {
      const date = addHours(new Date().setHours(0, 0, 0), i)
      datesTemp.push(date)
    }
    return datesTemp
  }, [])

  const minDate = dates[0]
  const maxDate = dates[dates.length - 1]

  return (
    <Box w={72} mt={mt ?? 30} h={h ?? 'full'} position="relative" {...props}>
      {dates.map((d) => (
        <Text
          position="absolute"
          transform="translateY(-50%)"
          top={calculatePosition(minDate.getTime(), maxDate.getTime(), d.getTime()) + '%'}
          margin={0}
          p={0}
          key={d.toISOString()}
        >
          {format(d, 'HH:mm')}
        </Text>
      ))}
    </Box>
  )
}
