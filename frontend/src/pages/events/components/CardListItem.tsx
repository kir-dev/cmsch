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
      marginTop={5}
      transition="transform .2s ease-in-out"
      _hover={{ transform: 'translateX(0.5em)' }}
    >
      <HStack spacing={4}>
        <VStack align="flex-start" overflow="hidden">
          <Heading as="h3" size="md" marginY={0} maxWidth="100%">
            {title}
          </Heading>
        </VStack>
        <Spacer />
        {open ? (
          <ChevronUpIcon boxSize={{ base: 10, md: 16 }} color="gray.300" />
        ) : (
          <ChevronDownIcon boxSize={{ base: 10, md: 16 }} color="gray.300" />
        )}
      </HStack>
    </Box>
  )
}
