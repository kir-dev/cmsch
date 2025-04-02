import { Card, CardBody, CardHeader, CardProps, Heading, useColorModeValue } from '@chakra-ui/react'
import { useStyleFromContext } from '../../api/contexts/config/ConfigContext.tsx'

export interface CmschContainerProps extends CardProps {
  title?: string
  disablePadding?: boolean
}

export const CmschContainer = ({ children, title, disablePadding, ...props }: CmschContainerProps) => {
  const theme = useStyleFromContext()
  return (
    <Card
      flexDirection="column"
      p={[0, null, 0]}
      mx="auto"
      maxW="100%"
      borderRadius={[0, null, 'xl']}
      w={['100%', '64rem']}
      backdropFilter={useColorModeValue(theme?.lightContainerFilter, theme?.darkContainerFilter)}
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
}
