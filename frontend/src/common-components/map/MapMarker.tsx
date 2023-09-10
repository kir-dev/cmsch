import { Box, BoxProps, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { MapMarkerShape } from '../../util/views/map.view'

interface MapMarkerProps {
  color?: BoxProps['color']
  text?: string
  mapShape?: MapMarkerShape
}

export function MapMarker({ color = 'brand.600', text, mapShape = MapMarkerShape.CIRCLE }: MapMarkerProps) {
  let borderRadius: BoxProps['borderRadius'] = 'full'
  if (mapShape === MapMarkerShape.SQUARE) borderRadius = 'md'
  const bg = useColorModeValue('white', 'gray.800')

  return (
    <VStack w="fit-content" spacing={1} marginLeft="calc(-50% + 10px)" marginTop="10px">
      <Box
        h="20px"
        w="20px"
        borderRadius={borderRadius}
        borderColor="white"
        borderWidth="2px"
        boxSizing="border-box"
        bg={color ?? 'brand.500'}
      />
      {text && (
        <Box bg={bg} py={0.5} px={2} borderRadius="full">
          <Text fontSize="xs">{text}</Text>
        </Box>
      )}
    </VStack>
  )
}
