import React from 'react'
import { Button, ButtonProps, Text, LinkProps as ChakraLinkProps, Link } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'
import { LinkIcon } from '@chakra-ui/icons'

export const Paragraph: React.FC = ({ children, ...props }) => {
  return (
    <Text marginTop={5} {...props} textAlign="justify">
      {children}
    </Text>
  )
}

type LinkProps = {
  external?: boolean
  href: string
}
export const LinkButton: React.FC<LinkProps & ButtonProps> = ({ external, href, children, ...props }) => {
  const ButtonComponent = <Button {...props}>{children}</Button>
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
