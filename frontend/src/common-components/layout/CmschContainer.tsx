import { Card, CardBody, CardProps, useColorModeValue } from '@chakra-ui/react'

export interface CmschContainerProps extends CardProps {}

export const CmschContainer = ({ children, ...props }: CmschContainerProps) => (
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
    <CardBody>{children}</CardBody>
  </Card>
)
