import React from 'react'
import { Text } from '@chakra-ui/react'

export const Paragraph: React.FC = ({ children, ...props }) => {
  return (
    <Text marginTop={5} {...props} textAlign="justify">
      {children}
    </Text>
  )
}
