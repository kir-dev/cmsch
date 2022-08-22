import { BoxProps, Flex, useColorModeValue } from '@chakra-ui/react'
import { HasChildren } from '../../util/react-types.util'

type Props = {} & HasChildren & BoxProps

export const CmschContainer = ({ children, ...props }: Props) => (
  <Flex
    flexDirection="column"
    px="4"
    py="4"
    mx="auto"
    maxWidth={['100%', '48rem']}
    borderRadius={[0, 0]}
    bg={useColorModeValue('lightContainerBg', 'darkContainerBg')}
    {...props}
  >
    {children}
  </Flex>
)
