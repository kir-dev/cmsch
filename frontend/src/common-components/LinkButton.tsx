import { Button, type ButtonProps } from '@/components/ui/button'
import type { FC } from 'react'
import { useNavigate } from 'react-router'

export type LinkProps = {
  external?: boolean
  href: string
  newTab?: boolean
}

export const LinkButton: FC<LinkProps & ButtonProps> = ({ external, href, children, newTab = true, ...props }) => {
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
        } else {
          navigate(href)
        }
      }}
    >
      {children}
    </Button>
  )
}
