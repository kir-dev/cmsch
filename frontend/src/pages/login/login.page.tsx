import { Key, LogIn } from 'lucide-react'
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
                <svg role="img" fill="currentColor" className="mr-2 h-4 w-4" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <title>Google</title>
                  {/* eslint-disable-next-line max-len */}
                  <path d="M12.48 10.92v3.28h7.84c-.24 1.84-.853 3.187-1.787 4.133-1.147 1.147-2.933 2.4-6.053 2.4-4.827 0-8.6-3.893-8.6-8.72s3.773-8.72 8.6-8.72c2.6 0 4.507 1.027 5.907 2.347l2.307-2.307C18.747 1.44 16.133 0 12.48 0 5.867 0 .307 5.387.307 12s5.56 12 12.173 12c3.573 0 6.267-1.173 8.373-3.36 2.16-2.16 2.84-5.213 2.84-7.667 0-.76-.053-1.467-.173-2.053H12.48z" />
                </svg>{' '}
                Google SSO
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
