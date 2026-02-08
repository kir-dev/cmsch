import { Link as ChakraLink, type LinkProps } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router'
import { useBrandColor } from '../util/core-functions.util.ts'

type Props = {
  isExternal?: boolean
  to: string
} & LinkProps

export const CmschLink = ({ isExternal, to, children, ...props }: Props) => {
  const Component = (
    <ChakraLink as="span" color={useBrandColor(500, 300)} {...props}>
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
