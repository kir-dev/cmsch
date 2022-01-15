import React from 'react'
import { Text } from '@chakra-ui/react'

export const Paragraph: React.FC = ({ children, ...props }) => {
  return (
    <Text my={5} {...props}>
      {children}
    </Text>
  )
}
