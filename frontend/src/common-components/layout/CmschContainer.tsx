import { Flex, useColorModeValue } from '@chakra-ui/react'
import { HasChildren } from '../../util/react-types.util'

type Props = {} & HasChildren

export const CmschContainer = ({ children }: Props) => (
  <Flex
    flexDirection="column"
    px="4"
    py="4"
    mx="auto"
    maxWidth={['100%', '48rem', '48rem', '64rem']}
    borderRadius={[0, 10]}
    bg={useColorModeValue('lightContainerBg', 'darkContainerBg')}
  >
    {children}
  </Flex>
)
