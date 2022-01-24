import React from 'react'
import { Button, ButtonProps } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'

export type LinkProps = {
  external?: boolean
  href: string
}

export const LinkButton: React.FC<LinkProps & ButtonProps> = ({ external, href, children, ...props }) => {
  const navigate = useNavigate()
  return (
    <Button
      {...props}
      onClick={() => {
        if (external) window.open(href)
        else navigate(href)
      }}
    >
      {children}
    </Button>
  )
}
