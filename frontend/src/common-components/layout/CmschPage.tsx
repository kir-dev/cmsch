import { CmschContainer } from './CmschContainer'
import { Outlet } from 'react-router-dom'
import { HasChildren } from '../../util/react-types.util'
//import { AbsolutePaths } from '../../util/paths'

type Props = {
  loginRequired?: boolean
  groupRequired?: boolean
} & HasChildren

export const CmschPage = ({ loginRequired, groupRequired, children, ...props }: Props) => {
  // const { isLoggedIn, profile } = useAuthContext()

  // if (loginRequired && !isLoggedIn) return <UnauthorizedPage />

  // if (groupRequired && profile?.groupSelectionAllowed) return <Navigate to={`${AbsolutePaths.PROFILE}tankor-modositas`} />

  return (
    <CmschContainer {...props}>
      <Outlet />
      {children}
    </CmschContainer>
  )
}
