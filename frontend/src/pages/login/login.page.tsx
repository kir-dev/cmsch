import axios from 'axios'
import { Chrome, Key, LogIn } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import ReCAPTCHA from 'react-google-recaptcha'
import { Navigate, useSearchParams } from 'react-router'

import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from '@/hooks/use-toast'
import { API_BASE_URL } from '@/util/configs/environment.config'
import { l } from '@/util/language'
import { ApiPaths } from '@/util/paths'

const LoginPage = () => {
  const { isLoggedIn, refetch } = useAuthContext()
  const component = useConfigContext()?.components?.login
  const { toast } = useToast()
  const [searchParams] = useSearchParams()
  const recaptchaRef = useRef<ReCAPTCHA>(null)

  useEffect(() => {
    if (searchParams.get('confirmed') === 'true') {
      toast({ title: 'Sikeres megerősítés', description: 'Az emailedet megerősítettük, most már bejelentkezhetsz.' })
    }
    if (searchParams.get('error') === 'invalid-token') {
      toast({ title: 'Hiba', description: 'Érvénytelen vagy lejárt megerősítő token.', variant: 'destructive' })
    }
  }, [searchParams, toast])

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [captchaToken, setCaptchaToken] = useState<string | null>(null)
  const [isRegistering, setIsRegistering] = useState(false)
  const [isForgotPassword, setIsForgotPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  if (!component) return <ComponentUnavailable />

  if (isLoggedIn) return <Navigate replace to="/" />

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    try {
      const response = await axios.post(`${API_BASE_URL}${ApiPaths.LOGIN}`, { email, password }, { withCredentials: true })
      if (response.data.status === 'ok') {
        toast({ title: 'Sikeres bejelentkezés' })
        refetch()
      } else {
        toast({ title: 'Hiba', description: response.data.message, variant: 'destructive' })
      }
    } catch (error) {
      console.error(error)
      reportError(error)
      toast({ title: 'Hiba', description: 'Hálózati hiba történt', variant: 'destructive' })
    } finally {
      setIsLoading(false)
    }
  }

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault()
    if (component.captchaEnabled && !captchaToken) {
      toast({ title: 'Hiba', description: 'Kérlek igazold vissza, hogy nem vagy robot!', variant: 'destructive' })
      return
    }
    setIsLoading(true)
    try {
      const response = await axios.post(
        `${API_BASE_URL}${ApiPaths.REGISTER}`,
        { email, password, fullName, captchaToken },
        { withCredentials: true }
      )
      if (response.data.status === 'ok') {
        toast({ title: 'Sikeres regisztráció', description: response.data.message })
        setIsRegistering(false)
        setCaptchaToken(null)
        recaptchaRef.current?.reset()
        refetch()
      } else {
        toast({ title: 'Hiba', description: response.data.message, variant: 'destructive' })
        recaptchaRef.current?.reset()
      }
    } catch (error) {
      console.error(error)
      reportError(error)
      toast({ title: 'Hiba', description: 'Hálózati hiba történt', variant: 'destructive' })
      recaptchaRef.current?.reset()
    } finally {
      setIsLoading(false)
    }
  }

  const handleForgotPassword = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    try {
      const response = await axios.post(`${API_BASE_URL}${ApiPaths.FORGOT_PASSWORD}`, { email }, { withCredentials: true })
      if (response.data.status === 'ok') {
        toast({ title: 'Kérés elküldve', description: response.data.message })
        setIsForgotPassword(false)
      } else {
        toast({ title: 'Hiba', description: response.data.message, variant: 'destructive' })
      }
    } catch (error) {
      console.error(error)
      reportError(error)
      toast({ title: 'Hiba', description: 'Hálózati hiba történt', variant: 'destructive' })
    } finally {
      setIsLoading(false)
    }
  }

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

        {component.passwordEnabled && (
          <Card className="w-full max-w-md shadow-lg">
            <CardHeader>
              <CardTitle className="text-center">
                {isForgotPassword ? 'Elfelejtett jelszó' : isRegistering ? 'Regisztráció' : 'Bejelentkezés'}
              </CardTitle>
            </CardHeader>
            <CardContent>
              {isForgotPassword ? (
                <div className="space-y-4">
                  <form onSubmit={handleForgotPassword}>
                    <div className="space-y-2">
                      <Label htmlFor="email">Email</Label>
                      <Input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                    </div>
                    <Button type="submit" className="w-full" disabled={isLoading}>
                      Visszaállítás kérése
                    </Button>
                  </form>
                  <Button variant="link" className="w-full mt-4" onClick={() => setIsForgotPassword(false)}>
                    Vissza a bejelentkezéshez
                  </Button>
                </div>
              ) : (
                <div>
                  <form onSubmit={isRegistering ? handleRegister : handleLogin} className="space-y-4">
                    {isRegistering && (
                      <div className="space-y-2">
                        <Label htmlFor="fullname">Teljes név</Label>
                        <Input id="fullname" type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} required />
                      </div>
                    )}
                    <div className="space-y-2">
                      <Label htmlFor="email">Email</Label>
                      <Input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="password">Jelszó</Label>
                      <Input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                    </div>
                    {isRegistering && component.captchaEnabled && (
                      <div className="flex justify-center py-2">
                        <ReCAPTCHA ref={recaptchaRef} sitekey={component.captchaSiteKey} onChange={(token) => setCaptchaToken(token)} />
                      </div>
                    )}
                    <Button type="submit" className="w-full" disabled={isLoading}>
                      {isRegistering ? 'Regisztráció' : 'Bejelentkezés'}
                    </Button>
                  </form>
                  <div className="flex flex-col items-center space-y-2 mt-4">
                    <Button variant="link" onClick={() => setIsRegistering(!isRegistering)}>
                      {isRegistering ? 'Van már fiókom' : 'Nincs még fiókom, regisztrálok'}
                    </Button>
                    {!isRegistering && component.forgotPasswordEnabled && (
                      <Button variant="link" size="sm" onClick={() => setIsForgotPassword(true)}>
                        Elfelejtettem a jelszavam
                      </Button>
                    )}
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        )}

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
