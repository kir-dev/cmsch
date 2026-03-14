import { useEffect, useRef, useState } from 'react'
import ReCAPTCHA from 'react-google-recaptcha'
import { useSearchParams } from 'react-router'

import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useForgotPasswordMutation, useLoginMutation, useLoginPoll, useRegisterMutation } from '@/api/hooks/auth/useAuthActions'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useToast } from '@/hooks/use-toast'

import { ForgotPasswordForm } from './components/ForgotPasswordForm'
import { LoginForm } from './components/LoginForm'
import { PollingAlert } from './components/PollingAlert'
import { RegisterForm } from './components/RegisterForm'
import { getMessageFromStatus } from './utils/loginUtils'

export const UsernamePasswordLogin = () => {
  const { toast } = useToast()
  const [searchParams] = useSearchParams()
  const recaptchaRef = useRef<ReCAPTCHA>(null)
  const component = useConfigContext()!.components!.login!

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [captchaToken, setCaptchaToken] = useState<string | null>(null)
  const [isRegistering, setIsRegistering] = useState(false)
  const [isForgotPassword, setIsForgotPassword] = useState(false)
  const [isPolling, setIsPolling] = useState(false)

  useEffect(() => {
    if (searchParams.get('confirmed') === 'true') {
      toast({ title: 'Sikeres megerősítés', description: 'Az emailedet megerősítettük, most már bejelentkezhetsz.' })
    }
    if (searchParams.get('error') === 'invalid-token') {
      toast({ title: 'Hiba', description: 'Érvénytelen vagy lejárt megerősítő token.', variant: 'destructive' })
    }
  }, [searchParams, toast])

  useLoginPoll(email, password, isPolling, () => {
    setIsPolling(false)
    toast({ title: 'Sikeres bejelentkezés' })
  })

  const loginMutation = useLoginMutation(
    (data) => {
      if (data.status === 'OK') {
        toast({ title: 'Sikeres bejelentkezés' })
      } else if (data.status === 'EMAIL_NOT_CONFIRMED') {
        toast({ title: 'Hiba', description: getMessageFromStatus(data.status), variant: 'destructive' })
        setIsPolling(true)
      } else {
        toast({ title: 'Hiba', description: getMessageFromStatus(data.status), variant: 'destructive' })
      }
    },
    () => {
      toast({ title: 'Hiba', description: 'Hálózati hiba történt', variant: 'destructive' })
    }
  )

  const registerMutation = useRegisterMutation(
    (data) => {
      if (data.status === 'OK') {
        toast({ title: 'Sikeres regisztráció', description: data.message || getMessageFromStatus(data.status) })
        if (!data.emailConfirmed) {
          setIsPolling(true)
        }
        setIsRegistering(false)
        setCaptchaToken(null)
        recaptchaRef.current?.reset()
      } else {
        toast({ title: 'Hiba', description: getMessageFromStatus(data.status), variant: 'destructive' })
        recaptchaRef.current?.reset()
      }
    },
    () => {
      toast({ title: 'Hiba', description: 'Hálózati hiba történt', variant: 'destructive' })
      recaptchaRef.current?.reset()
    }
  )

  const forgotPasswordMutation = useForgotPasswordMutation(
    (data) => {
      if (data.status === 'OK') {
        toast({ title: 'Kérés elküldve', description: data.message || getMessageFromStatus(data.status) })
        setIsForgotPassword(false)
      } else {
        toast({ title: 'Hiba', description: getMessageFromStatus(data.status), variant: 'destructive' })
      }
    },
    () => {
      toast({ title: 'Hiba', description: 'Hálózati hiba történt', variant: 'destructive' })
    }
  )

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault()
    loginMutation.mutate({ email, password })
  }

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault()
    if (component.captchaEnabled && !captchaToken) {
      toast({ title: 'Hiba', description: 'Kérlek igazold vissza, hogy nem vagy robot!', variant: 'destructive' })
      return
    }
    registerMutation.mutate({ email, password, fullName, captchaToken })
  }

  const handleForgotPassword = (e: React.FormEvent) => {
    e.preventDefault()
    forgotPasswordMutation.mutate(email)
  }

  const isLoading = loginMutation.isPending || registerMutation.isPending || forgotPasswordMutation.isPending

  const renderContent = () => {
    if (isForgotPassword) {
      return (
        <ForgotPasswordForm
          email={email}
          setEmail={setEmail}
          onSubmit={handleForgotPassword}
          onBack={() => setIsForgotPassword(false)}
          isLoading={isLoading}
        />
      )
    }

    if (isRegistering) {
      return (
        <RegisterForm
          fullName={fullName}
          setFullName={setFullName}
          email={email}
          setEmail={setEmail}
          password={password}
          setPassword={setPassword}
          captchaToken={captchaToken}
          setCaptchaToken={setCaptchaToken}
          recaptchaRef={recaptchaRef}
          onSubmit={handleRegister}
          onToggleLogin={() => setIsRegistering(false)}
          isLoading={isLoading}
          isPolling={isPolling}
          captchaEnabled={component.captchaEnabled}
          captchaSiteKey={component.captchaSiteKey}
        />
      )
    }

    return (
      <LoginForm
        email={email}
        setEmail={setEmail}
        password={password}
        setPassword={setPassword}
        onSubmit={handleLogin}
        onToggleRegister={() => setIsRegistering(true)}
        onForgotPassword={() => setIsForgotPassword(true)}
        isLoading={isLoading}
        isPolling={isPolling}
        forgotPasswordEnabled={component.forgotPasswordEnabled}
      />
    )
  }

  return (
    <Card className="w-full max-w-md shadow-lg">
      <CardHeader>
        <CardTitle className="text-center">
          {isForgotPassword ? 'Elfelejtett jelszó' : isRegistering ? 'Regisztráció' : 'Bejelentkezés'}
        </CardTitle>
      </CardHeader>
      <CardContent>
        {isPolling && <PollingAlert />}
        {renderContent()}
      </CardContent>
    </Card>
  )
}
