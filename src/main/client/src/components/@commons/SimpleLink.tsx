import React from 'react'
import { LinkProps as ChakraLinkProps } from '@chakra-ui/layout'
import { Link } from '@chakra-ui/react'
import { LinkIcon } from '@chakra-ui/icons'
import { Link as RouterLink } from 'react-router-dom'
import { LinkProps } from './LinkButton'

export const SimpleLink: React.FC<LinkProps & ChakraLinkProps> = ({ external, href, children, ...props }) => {
  const ButtonComponent = (
    <Link {...props}>
      {children}
      <LinkIcon marginLeft={1} />
    </Link>
  )
  return external ? (
    <a href={href} target="_blank" rel="noreferrer">
      {ButtonComponent}
    </a>
  ) : (
    <RouterLink to={href} replace>
      {ButtonComponent}
    </RouterLink>
  )
}
