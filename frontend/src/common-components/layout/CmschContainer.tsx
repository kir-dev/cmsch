import { Card, CardBody, CardHeader, CardProps, Heading, useColorModeValue } from '@chakra-ui/react'

export interface CmschContainerProps extends CardProps {
  title?: string
  disablePadding?: boolean
}

export const CmschContainer = ({ children, title, disablePadding, ...props }: CmschContainerProps) => (
  <Card
    flexDirection="column"
    p={[0, null, 0]}
    mx="auto"
    maxW="100%"
    borderRadius={[0, null, 'xl']}
    w={['100%', '64rem']}
    bg={useColorModeValue('lightContainerBg', 'darkContainerBg')}
    {...props}
  >
    {title && (
      <CardHeader>
        <Heading>{title}</Heading>
      </CardHeader>
    )}
    <CardBody p={disablePadding ? 0 : undefined}>{children}</CardBody>
  </Card>
)
