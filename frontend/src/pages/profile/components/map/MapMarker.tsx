import { Box, BoxProps, Center, Text, useColorModeValue, VStack } from '@chakra-ui/react'

interface MapMarkerProps {
  color?: BoxProps['color']
  text?: string
}

export function MapMarker({ color, text }: MapMarkerProps) {
  const bg = useColorModeValue('white', 'gray.800')
  return (
    <VStack w="fit-content" spacing={1} marginLeft="-50%" marginTop="-10px">
      <Center h="20px" w="20px" borderRadius="full" bg="white" dropShadow="lg">
        <Box h="15px" w="15px" borderRadius="full" bg={color ?? 'brand.500'} />
      </Center>
      {text && (
        <Box bg={bg} py={0.5} px={2} borderRadius="full">
          <Text fontSize="xs">{text}</Text>
        </Box>
      )}
    </VStack>
  )
}
