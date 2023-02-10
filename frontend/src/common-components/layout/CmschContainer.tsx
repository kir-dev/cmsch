import { Card, CardBody, CardHeader, CardProps, useColorModeValue } from '@chakra-ui/react'

export interface CmschContainerProps extends CardProps {
  title?: string
}

export const CmschContainer = ({ children, title, ...props }: CmschContainerProps) => (
  <Card
    flexDirection="column"
    px="4"
    py="4"
    mx="auto"
    w="100%"
    maxWidth={['100%', '48rem']}
    bg={useColorModeValue('lightContainerBg', 'darkContainerBg')}
    {...props}
  >
    {title && <CardHeader>{title}</CardHeader>}
    <CardBody>{children}</CardBody>
  </Card>
)
