import { BoxProps, HStack, Text, useColorModeValue } from '@chakra-ui/react'
import { PulsingDot } from './PulsingDot'

interface EventIndicatorProps extends BoxProps {
  isCurrent: boolean
  isUpcoming: boolean
  showLabel?: boolean
}

export function EventIndicator({ isCurrent, isUpcoming, color, showLabel, ...props }: EventIndicatorProps) {
  const bg = useColorModeValue('#00000020', '#FFFFFF20')
  if (!isCurrent && !isUpcoming) return null
  if (showLabel)
    return (
      <HStack backgroundColor={bg} py={1} pl={4} pr={1} borderRadius="full" {...props}>
        <Text>{isUpcoming ? 'Hamarosan kezdődik' : 'Most zajlik'}</Text>
        <PulsingDot color={color ?? isUpcoming ? 'yellow.400' : undefined} />
      </HStack>
    )
  return <PulsingDot color={color ?? isUpcoming ? 'yellow.400' : undefined} {...props} />
}
