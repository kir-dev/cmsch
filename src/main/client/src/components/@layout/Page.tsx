import React from 'react'
import { Container } from './Container'
import { Outlet } from 'react-router-dom'
import { UnauthorizedPage } from '../@pages/UnauthorizedPage'
import { useAuthContext } from '../../utils/useAuthContext'

type PageProps = {
  loginRequired?: boolean
  title?: string
}

export const Page: React.FC<PageProps> = ({ loginRequired, title, children, ...props }) => {
  const { isLoggedIn } = useAuthContext()
  if (loginRequired && !isLoggedIn) return <UnauthorizedPage />
  return (
    <>
      <Container {...props}>
        <Outlet />
        {children}
      </Container>
    </>
  )
}
