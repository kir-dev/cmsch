import { Text, TextProps } from '@chakra-ui/react'
import { FC } from 'react'

export const Paragraph: FC<TextProps> = ({ children, textAlign, ...props }) => (
  <Text marginTop={5} {...props} textAlign={textAlign}>
    {children}
  </Text>
)
