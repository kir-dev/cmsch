import React from 'react'
import { Button, ButtonProps } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'

export type LinkProps = {
  external?: boolean
  href: string
  newTab?: boolean
}

export const LinkButton: React.FC<LinkProps & ButtonProps> = ({ external, href, children, newTab = true, ...props }) => {
  const navigate = useNavigate()
  return (
    <Button
      {...props}
      onClick={() => {
        if (external) {
          if (newTab) {
            window.open(href)
          } else {
            location.href = href
          }
        } else navigate(href)
      }}
    >
      {children}
    </Button>
  )
}
