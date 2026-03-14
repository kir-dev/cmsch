import { Chrome, Key, LogIn } from 'lucide-react'
import { Navigate } from 'react-router'

import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { API_BASE_URL } from '@/util/configs/environment.config'
import { l } from '@/util/language'
import { UsernamePasswordLogin } from './UsernamePasswordLogin'

const LoginPage = () => {
  const { isLoggedIn } = useAuthContext()
  const component = useConfigContext()?.components?.login

  if (!component) return <ComponentUnavailable />

  if (isLoggedIn) return <Navigate replace to="/" />

  return (
    <CmschPage title={l('login-helmet')}>
      <div className="flex flex-col items-center space-y-10 mb-10">
        {component.topMessage && !component.passwordEnabled ? (
          <div className="text-center">
            <Markdown text={component.topMessage} />
          </div>
        ) : (
          <div className="mt-4" />
        )}

        {component.passwordEnabled && <UsernamePasswordLogin />}

        <div className="flex flex-col items-center space-y-4 w-full max-w-md">
          {component.authschPromoted && (
            <>
              <Button className="w-full" onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/authsch`)}>
                <LogIn className="mr-2 h-4 w-4" /> {component.onlyBmeProvider ? 'BME Címtár' : 'AuthSCH'}
              </Button>
              {(component.googleSsoEnabled || component.keycloakEnabled) && <span>vagy</span>}
            </>
          )}
          {component.googleSsoEnabled && (
            <>
              <Button className="w-full" onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/google`)}>
                <Chrome className="mr-2 h-4 w-4" /> Google SSO
              </Button>
              {component.keycloakEnabled && <span>vagy</span>}
            </>
          )}
          {component.keycloakEnabled && (
            <>
              <Button className="w-full" onClick={() => (window.location.href = `${API_BASE_URL}/oauth2/authorization/keycloak`)}>
                <Key className="mr-2 h-4 w-4" /> {component.keycloakAuthName}
              </Button>
            </>
          )}
        </div>
        {component.bottomMessage && (
          <div className="max-w-md text-center">
            <Markdown text={component.bottomMessage} />
          </div>
        )}
      </div>
    </CmschPage>
  )
}

export default LoginPage
