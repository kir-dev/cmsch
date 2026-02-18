import { Navigate, Outlet } from 'react-router'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { LoadingPage } from '../../pages/loading/loading.page'
import { Title } from '../../util/TitleProvider.tsx'
import { RoleType } from '../../util/views/profile.view'
import { LoginRequired } from '../LoginRequired'
import { CmschContainer, type CmschContainerProps } from './CmschContainer'

interface CmschPageProps extends CmschContainerProps {
  loginRequired?: boolean
  minRole?: RoleType
}

export const CmschPage = ({ children, loginRequired, minRole, title, ...props }: CmschPageProps) => {
  const { authInfo, authInfoLoading, isLoggedIn } = useAuthContext()
  if (loginRequired) {
    if (authInfoLoading) return <LoadingPage />
    if (!isLoggedIn) return <LoginRequired />
  }
  if (minRole && minRole > 0) {
    if (!authInfo?.role || RoleType[authInfo?.role] < minRole) {
      return <Navigate to="/" replace />
    }
  }

  return (
    <CmschContainer {...props} pb={10}>
      <Title text={title} />
      <Outlet />
      {children}
    </CmschContainer>
  )
}
