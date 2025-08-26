import { useEffect } from 'react'
import { Navigate, useLocation } from 'react-router'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

const IndexPage = () => {
  const location = useLocation()
  const { onLogout } = useAuthContext()
  const config = useConfigContext()

  useEffect(() => {
    if (location.pathname === '/logout') {
      onLogout()
    }
  }, [location, onLogout])

  return <Navigate to={config?.components?.app?.defaultComponent || '/home'} />
}

export default IndexPage
