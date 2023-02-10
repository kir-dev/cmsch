import { CmschContainer, CmschContainerProps } from './CmschContainer'
import { Navigate, Outlet } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { RoleType } from '../../util/views/profile.view'
import { Loading } from '../Loading'

//import { AbsolutePaths } from '../../util/paths'

interface CmschPageProps extends CmschContainerProps {
  loginRequired?: boolean
  groupRequired?: boolean
  minRole?: RoleType
}

export const CmschPage = ({ loginRequired, groupRequired, children, minRole, ...props }: CmschPageProps) => {
  const { profile, profileLoading } = useAuthContext()

  if (minRole && minRole > 0) {
    if (profileLoading) {
      return <Loading />
    } else if (!profile?.role || RoleType[profile?.role] < minRole) {
      return <Navigate to="/" replace />
    }
  }
  // if (loginRequired && !isLoggedIn) return <UnauthorizedPage />

  // if (groupRequired && profile?.groupSelectionAllowed) return <Navigate to={`${AbsolutePaths.PROFILE}tankor-modositas`} />

  return (
    <CmschContainer {...props} pb={10}>
      <Outlet />
      {children}
    </CmschContainer>
  )
}
