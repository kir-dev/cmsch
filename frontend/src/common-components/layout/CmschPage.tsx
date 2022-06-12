import { CmschContainer } from './CmschContainer'
import { Navigate, Outlet } from 'react-router-dom'
import { HasChildren } from '../../util/react-types.util'

type Props = {
  loginRequired?: boolean
  groupRequired?: boolean
} & HasChildren

export const CmschPage = ({ loginRequired, groupRequired, children, ...props }: Props) => {
  // const { isLoggedIn, profile } = useAuthContext()

  // if (loginRequired && !isLoggedIn) return <UnauthorizedPage />

  // if (groupRequired && profile?.groupSelectionAllowed) return <Navigate to="/profil/tankor-modositas" />

  return (
    <CmschContainer {...props}>
      <Outlet />
      {children}
    </CmschContainer>
  )
}
