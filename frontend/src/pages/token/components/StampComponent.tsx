import { Box, Center, Flex, Icon, Text } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { FC } from 'react'
import { FaRocket, FaStamp } from 'react-icons/fa'
import { IconType } from 'react-icons/lib'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'

interface StampComponentProps {
  title?: string
  type: string
}

export const StampComponent: FC<StampComponentProps> = ({ title, type }: StampComponentProps) => {
  const backgroundBase = useColorModeValue('gray.100', 'gray.700')
  const stampCorner = useColorModeValue('gray.800', 'gray.200')
  const component = useConfigContext()?.components.token

  const icon: IconType = type === component?.collectType ? FaStamp : FaRocket

  return (
    <Box maxW="md" minW={['100%', 'md']} borderRadius="lg" bg={backgroundBase}>
      <Flex>
        <Center bg={stampCorner} padding="2" borderStartRadius="lg">
          <Icon as={icon} boxSize="2em" fontSize="3xl" color={useColorModeValue('brand.500', 'brand.600')} />
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
