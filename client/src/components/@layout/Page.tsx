import React from 'react'
import { Container } from './Container'
import { Navigate, Outlet } from 'react-router-dom'
import { UnauthorizedPage } from '../@pages/UnauthorizedPage'
import { useAuthContext } from '../../utils/useAuthContext'

type PageProps = {
  loginRequired?: boolean
  groupRequired?: boolean
}

export const Page: React.FC<PageProps> = ({ loginRequired, groupRequired, children, ...props }) => {
  const { isLoggedIn, profile } = useAuthContext()
  if (loginRequired && !isLoggedIn) return <UnauthorizedPage />
  if (groupRequired && profile?.groupSelectionAllowed) return <Navigate to="/profil/tankor-modositas" />
  return (
    <>
      <Container {...props}>
        <Outlet />
        {children}
      </Container>
    </>
  )
}
