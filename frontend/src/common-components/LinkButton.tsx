import { Button, ButtonProps } from '@chakra-ui/react'
import { FC } from 'react'
import { useNavigate } from 'react-router-dom'

export type LinkProps = {
  external?: boolean
  href: string
  newTab?: boolean
  onClick?: () => void
}

export const LinkButton: FC<LinkProps & ButtonProps> = ({ external, href, children, newTab = true, onClick = () => {}, ...props }) => {
  const navigate = useNavigate()
  return (
    <Button
      {...props}
      onClick={() => {
        onClick()
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
