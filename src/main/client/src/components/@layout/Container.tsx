import * as React from 'react'
import { Flex } from '@chakra-ui/react'

export const Container: React.FC = ({ children }) => (
  <Flex flexDirection="column" px="4" mx="auto" maxWidth={['100%', '48rem', '48rem', '64rem']}>
    {children}
  </Flex>
)
