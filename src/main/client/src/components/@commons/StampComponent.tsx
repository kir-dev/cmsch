import { Box, Center, Flex, Icon, Text } from '@chakra-ui/react'
import React from 'react'
import { useColorModeValue } from '@chakra-ui/system'
import { FaRocket, FaStamp } from 'react-icons/fa'
import { IconType } from 'react-icons/lib'

interface StampComponentProps {
  title?: string
  type: string
}

export const StampComponent: React.FC<StampComponentProps> = ({ title, type }: StampComponentProps) => {
  const backgroundBase = useColorModeValue('gray.100', 'gray.700')
  const stampCorner = useColorModeValue('gray.200', 'gray.600')

  const icon: IconType = type === 'default' ? FaStamp : FaRocket

  return (
    <Box maxW="md" minW={['100%', 'md']} borderRadius="lg" bg={backgroundBase}>
      <Flex>
        <Center bg={stampCorner} padding="2" borderStartRadius="lg">
          <Icon as={icon} boxSize="2em" fontSize="3xl" color="brand.500" />
        </Center>
        <Center width="100%" paddingStart="3" textAlign="center">
          <Text fontSize="xl" fontWeight="bold">
            {title}
          </Text>
        </Center>
      </Flex>
    </Box>
  )
}
