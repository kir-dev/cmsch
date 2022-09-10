import { CmschContainer } from './CmschContainer'
import { Navigate, Outlet } from 'react-router-dom'
import { HasChildren } from '../../util/react-types.util'
import { BoxProps } from '@chakra-ui/react'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { RoleType } from '../../util/views/profile.view'
import { Loading } from '../Loading'
//import { AbsolutePaths } from '../../util/paths'

type Props = {
  loginRequired?: boolean
  groupRequired?: boolean
  minRole?: RoleType
} & HasChildren &
  BoxProps

export const CmschPage = ({ loginRequired, groupRequired, children, minRole, ...props }: Props) => {
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
