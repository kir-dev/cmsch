import React from 'react'
import { Container } from './Container'
import { Outlet } from 'react-router-dom'
import { UnauthorizedPage } from '../@pages/UnauthorizedPage'
import { useAuthContext } from '../../utils/useAuthContext'

type PageProps = {
  loginRequired?: boolean
}

export const Page: React.FC<PageProps> = ({ loginRequired, children, ...props }) => {
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
