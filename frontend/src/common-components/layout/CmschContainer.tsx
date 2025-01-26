import { Card, CardBody, CardHeader, CardRootProps, Heading } from '@chakra-ui/react'
import { useColorModeValue } from '../../components/ui/color-mode.tsx'

export interface CmschContainerProps extends CardRootProps {
  title?: string
  disablePadding?: boolean
}

export const CmschContainer = ({ children, title, disablePadding, ...props }: CmschContainerProps) => (
  <Card.Root
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
  </Card.Root>
)
