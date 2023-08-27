import { Divider } from '@chakra-ui/react'
import { calculatePosition } from './utils'

interface CurrentDateBarProps {
  minTimestamp: number
  maxTimestamp: number
}

export function CurrentDateBar({ maxTimestamp, minTimestamp }: CurrentDateBarProps) {
  const now = Date.now()
  if (now < minTimestamp || now > maxTimestamp) return null
  const position = calculatePosition(minTimestamp, maxTimestamp, now)
  return (
    <Divider
      opacity={1}
      zIndex={10}
      position="absolute"
      top={position + '%'}
      left={0}
      right={0}
      borderColor="red.500"
      borderWidth={2}
      borderRadius="full"
    />
  )
}
