import React from 'react'
import { Button, ButtonProps } from '@chakra-ui/react'
import { Link as RouterLink } from 'react-router-dom'

export type LinkProps = {
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
