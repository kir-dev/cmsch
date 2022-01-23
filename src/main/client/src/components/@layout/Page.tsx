import React from 'react'
import { Container } from './Container'
import { Outlet } from 'react-router-dom'
import { UnauthorizedPage } from '../@pages/UnauthorizedPage'
import { useAuthContext } from '../../utils/useAuthContext'
import { IndexLayout } from './IndexLayout'

type PageProps = {
  loginRequired?: boolean
}

export const Page: React.FC<PageProps> = ({ loginRequired, children, ...props }) => {
  const { isLoggedIn } = useAuthContext()
  if (loginRequired && !isLoggedIn) return <UnauthorizedPage />
  return (
    <IndexLayout>
      <Container {...props}>
        <Outlet />
        {children}
      </Container>
    </IndexLayout>
  )
}
