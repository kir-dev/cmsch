import { FC } from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import { useAuthContext } from '../../utils/useAuthContext'
import { UnauthorizedPage } from '../@pages/UnauthorizedPage'
import { Container } from './Container'

type PageProps = {
  loginRequired?: boolean
  groupRequired?: boolean
}

export const Page: FC<PageProps> = ({ loginRequired, groupRequired, children, ...props }) => {
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
