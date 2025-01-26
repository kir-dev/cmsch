import { HStack, IconButton, Text } from '@chakra-ui/react'
import { FaMinusCircle, FaPlusCircle } from 'react-icons/fa'

interface ZoomBarProps {
  scale: number
  incrementScale: () => void
  decrementScale: () => void
}

export function ZoomBar({ scale, incrementScale, decrementScale }: ZoomBarProps) {
  return (
    <HStack justify="center" mt={2}>
      <IconButton colorScheme="brand" aria-label="Kicsinyítés" onClick={decrementScale}>
        <FaMinusCircle />
      </IconButton>
      <Text>{Math.round(scale * 100)}%</Text>
      <IconButton colorScheme="brand" aria-label="Nagyítás" onClick={incrementScale}>
        <FaPlusCircle />
      </IconButton>
    </HStack>
  )
}
