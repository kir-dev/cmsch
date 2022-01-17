import { Box, Center, Flex, Icon, Stat, StatHelpText, StatLabel } from '@chakra-ui/react'
import React from 'react'
import { FaStamp } from 'react-icons/fa'

interface StampComponentProps {
  title?: string
  type: string
}

export const StampComponent: React.FC<StampComponentProps> = ({ title, type }: StampComponentProps) => {
  return (
    <Box maxW={'md'} minW={['100%', 'md']} borderRadius={'lg'} boxShadow="lg" bg="brand.100">
      <Flex>
        <Center bg={'brand.300'} padding="2" borderStartRadius={'lg'}>
          <Icon as={FaStamp} boxSize="2em" fontSize="3xl" color="black" />
        </Center>
        <Center width="100%" paddingStart={'3'} textAlign="center">
          <Stat>
            <StatLabel color="black" fontSize="xl">
              {title}
            </StatLabel>
            <StatHelpText color="gray.800">{type}</StatHelpText>
          </Stat>
        </Center>
      </Flex>
    </Box>
  )
}
