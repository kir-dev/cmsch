import { ChevronDownIcon, ChevronUpIcon } from '@chakra-ui/icons'
import { Box, BoxProps, Heading, HStack, Spacer, useColorModeValue } from '@chakra-ui/react'
import { PulsingDot } from '../../../common-components/PulsingDot'

type CardListItemProps = {
  title: string
  open: boolean
  toggle: () => void
  showPulsingDot?: boolean
  pulsingDotColor?: BoxProps['color']
}

export const CardListItem = ({ title, open, toggle, showPulsingDot, pulsingDotColor }: CardListItemProps) => {
  const bg = useColorModeValue('#00000020', '#FFFFFF20')
  const hoverBg = useColorModeValue('#00000030', '#FFFFFF30')
  return (
    <Box onClick={toggle} borderRadius="lg" padding={4} backgroundColor={bg} _hover={{ bg: hoverBg }} marginTop={2} cursor="pointer">
      <HStack>
        <Heading as="h3" size="md" marginY={0} maxWidth="100%">
          {title}
        </Heading>
        <Spacer />
        <HStack>
          {showPulsingDot && <PulsingDot color={pulsingDotColor} />}
          {open ? (
            <ChevronUpIcon boxSize={{ base: 5, md: 8 }} color={useColorModeValue('gray.700', 'gray.300')} />
          ) : (
            <ChevronDownIcon boxSize={{ base: 5, md: 8 }} color={useColorModeValue('gray.700', 'gray.300')} />
          )}
        </HStack>
      </HStack>
    </Box>
  )
}
