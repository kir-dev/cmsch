import { Link as ChakraLink, LinkProps } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { useColorModeValue } from '../components/ui/color-mode.tsx'

type Props = {
  isExternal?: boolean
  to: string
} & LinkProps

export const CmschLink = ({ isExternal, to, children, ...props }: Props) => {
  const Component = (
    <ChakraLink as="span" color={useColorModeValue('brand.500', 'brand.300')} {...props}>
      {children}
    </ChakraLink>
  )

  return isExternal ? (
    <a href={to} target="_blank" rel="noreferrer">
      {Component}
    </a>
  ) : (
    <RouterLink to={to}>{Component}</RouterLink>
  )
}
