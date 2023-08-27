import { Box, StackProps, Text } from '@chakra-ui/react'
import { addHours, endOfDay, format, startOfDay } from 'date-fns'
import { useMemo } from 'react'
import { calculatePosition } from './utils'

export function HourColumn({ position, ...props }: StackProps) {
  const originDate = startOfDay(new Date())
  const dates = useMemo(() => {
    const datesTemp: Date[] = []
    for (let i = 0; i < 24; i++) {
      const date = addHours(originDate, i)
      datesTemp.push(date)
    }
    return datesTemp
  }, [])

  const minDate = originDate
  const maxDate = endOfDay(originDate)

  return (
    <Box w={12} position={position ?? 'relative'} {...props}>
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
