import { ChevronUpIcon, ChevronDownIcon } from '@chakra-ui/icons'
import { Box, Heading, HStack, Spacer, useColorModeValue, VStack } from '@chakra-ui/react'

type CardListItemProps = {
  title: string
  open: boolean
  toggle: () => void
}

export const CardListItem = ({ title, open, toggle }: CardListItemProps) => {
  return (
    <Box
      onClick={toggle}
      borderRadius="lg"
      padding={4}
      backgroundColor={useColorModeValue('gray.100', 'gray.700')}
      _hover={{ bg: useColorModeValue('gray.200', 'gray.600') }}
      marginTop={2}
      cursor="pointer"
    >
      <HStack>
        <Heading as="h3" size="md" marginY={0} maxWidth="100%">
          {title}
        </Heading>
        <Spacer />
        {open ? (
          <ChevronUpIcon boxSize={{ base: 5, md: 8 }} color={useColorModeValue('gray.700', 'gray.300')} />
        ) : (
          <ChevronDownIcon boxSize={{ base: 5, md: 8 }} color={useColorModeValue('gray.700', 'gray.300')} />
        )}
      </HStack>
    </Box>
  )
}
