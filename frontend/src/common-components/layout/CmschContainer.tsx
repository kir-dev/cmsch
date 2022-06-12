import * as React from 'react'
import { Flex } from '@chakra-ui/react'
import { HasChildren } from '../../util/react-types.util'

type Props = {} & HasChildren

export const CmschContainer = ({ children }: Props) => (
  <Flex flexDirection="column" px="4" mx="auto" maxWidth={['100%', '48rem', '48rem', '64rem']}>
    {children}
  </Flex>
)
