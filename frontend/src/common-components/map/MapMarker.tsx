import { Box, BoxProps, Center, Text, VStack } from '@chakra-ui/react'
import { getTextColorFromLuminance } from '../../util/color.utils'
import { MapMarkerIcons, MapMarkerShape } from '../../util/views/map.view'
import { useColorModeValue } from '../../components/ui/color-mode.tsx'
import { FunctionComponent } from 'react'

interface MapMarkerProps {
  color?: string
  text?: string
  markerShape?: MapMarkerShape
}

export function MapMarker({ color = 'brand.600', text, markerShape = MapMarkerShape.CIRCLE }: MapMarkerProps) {
  let borderRadius: BoxProps['borderRadius'] = 'full'
  if (markerShape === MapMarkerShape.SQUARE) borderRadius = 'md'

  let Icon: FunctionComponent<any> = () => null
  if (Object.keys(MapMarkerIcons).includes(markerShape)) Icon = MapMarkerIcons[markerShape]
  const bg = useColorModeValue('white', 'gray.800')

  return (
    <VStack w={200} gap={1}>
      <Center
        h={6}
        w={6}
        borderRadius={borderRadius}
        borderColor="white"
        borderWidth="2px"
        boxSizing="border-box"
        bg={color ?? 'brand.500'}
      >
        <Icon color={getTextColorFromLuminance(color)} size={12} />
      </Center>
      {text && (
        <Box bg={bg} py={0.5} px={2} borderRadius="full" maxW="full">
          <Text fontSize="xs" truncate>
            {text}
          </Text>
        </Box>
      )}
    </VStack>
  )
}
