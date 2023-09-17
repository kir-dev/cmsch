import { Box, BoxProps, Center, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { getTextColorFromLuminance } from '../../util/color.utils'
import { MapMarkerIcons, MapMarkerShape } from '../../util/views/map.view'

interface MapMarkerProps {
  color?: string
  text?: string
  markerShape?: MapMarkerShape
}

export function MapMarker({ color = 'brand.600', text, markerShape = MapMarkerShape.CIRCLE }: MapMarkerProps) {
  let borderRadius: BoxProps['borderRadius'] = 'full'
  if (markerShape === MapMarkerShape.SQUARE) borderRadius = 'md'
  let icon = null
  if (Object.keys(MapMarkerIcons).includes(markerShape))
    icon = MapMarkerIcons[markerShape]({ color: getTextColorFromLuminance(color), size: 12 })
  const bg = useColorModeValue('white', 'gray.800')

  return (
    <VStack w={200} spacing={1}>
      <Center
        h={6}
        w={6}
        borderRadius={borderRadius}
        borderColor="white"
        borderWidth="2px"
        boxSizing="border-box"
        bg={color ?? 'brand.500'}
      >
        {icon}
      </Center>
      {text && (
        <Box bg={bg} py={0.5} px={2} borderRadius="full" maxW="full">
          <Text fontSize="xs" isTruncated>
            {text}
          </Text>
        </Box>
      )}
    </VStack>
  )
}
