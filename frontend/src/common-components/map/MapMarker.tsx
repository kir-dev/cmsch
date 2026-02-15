import { Box, type BoxProps, Center, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import type { FunctionComponent } from 'react'
import { getTextColorFromLuminance } from '../../util/color.utils'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { MapMarkerIcons, MapMarkerShape } from '../../util/views/map.view'

interface MapMarkerProps {
  color: string
  text?: string
  markerShape?: MapMarkerShape
}

export function MapMarker({ color, text, markerShape = MapMarkerShape.CIRCLE }: MapMarkerProps) {
  let borderRadius: BoxProps['borderRadius'] = 'full'
  if (markerShape === MapMarkerShape.SQUARE) borderRadius = 'md'
  let Icon: FunctionComponent<{ color: string; size: number }> = () => null
  if (Object.keys(MapMarkerIcons).includes(markerShape)) Icon = MapMarkerIcons[markerShape]
  const bg = useColorModeValue('white', 'gray.800')
  const brandColor = useBrandColor(500, 500)

  return (
    <VStack w={200} spacing={1}>
      <Center h={6} w={6} borderRadius={borderRadius} borderColor="white" borderWidth="2px" boxSizing="border-box" bg={color ?? brandColor}>
        {/* eslint-disable-next-line react-hooks/static-components */}
        <Icon color={getTextColorFromLuminance(color)} size={12} />
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
