import { HStack, IconButton, Text } from '@chakra-ui/react'
import { FaMinusCircle, FaPlusCircle } from 'react-icons/fa'
import { useBrandColor } from '../../../../util/core-functions.util.ts'

interface ZoomBarProps {
  scale: number
  incrementScale: () => void
  decrementScale: () => void
}

export function ZoomBar({ scale, incrementScale, decrementScale }: ZoomBarProps) {
  const brandColor = useBrandColor()
  return (
    <HStack justify="center" mt={2}>
      <IconButton colorScheme={brandColor} aria-label="Kicsinyítés" icon={<FaMinusCircle />} onClick={decrementScale} />
      <Text>{Math.round(scale * 100)}%</Text>
      <IconButton colorScheme={brandColor} aria-label="Nagyítás" icon={<FaPlusCircle />} onClick={incrementScale} />
    </HStack>
  )
}
