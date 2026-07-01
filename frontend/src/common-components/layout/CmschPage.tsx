import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { cn } from '@/lib/utils.ts'
import { LoadingPage } from '@/pages/loading/loading.page'
import { Title } from '@/util/TitleProvider.tsx'
import { RoleType } from '@/util/views/profile.view'
import { Navigate, Outlet } from 'react-router'
import { LoginRequired } from '../LoginRequired'
import { CmschContainer, type CmschContainerProps } from './CmschContainer'

interface CmschPageProps extends CmschContainerProps {
  loginRequired?: boolean
  minRole?: RoleType
}

export const CmschPage = ({ children, className, loginRequired, minRole, title, ...props }: CmschPageProps) => {
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
    <CmschContainer {...props} className={cn('pb-10', className)}>
      <Title text={title} />
      <Outlet />
      {children}
    </CmschContainer>
  )
}
