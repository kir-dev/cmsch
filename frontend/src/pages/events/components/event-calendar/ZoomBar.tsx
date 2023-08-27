import { HStack, IconButton, Text } from '@chakra-ui/react'
import { FaMinusCircle, FaPlusCircle } from 'react-icons/fa'

interface ZoomBarProps {
  scale: number
  incrementScale: () => void
  decrementScale: () => void
}

export function ZoomBar({ scale, incrementScale, decrementScale }: ZoomBarProps) {
  return (
    <HStack justify="center">
      <IconButton aria-label="Kicsinyítés" icon={<FaMinusCircle />} onClick={decrementScale} />
      <Text>{Math.round(scale * 100)}%</Text>
      <IconButton aria-label="Nagyítás" icon={<FaPlusCircle />} onClick={incrementScale} />
    </HStack>
  )
}
