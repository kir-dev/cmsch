import ReCAPTCHA from 'react-google-recaptcha'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

interface RegisterFormProps {
  fullName: string
  setFullName: (val: string) => void
  email: string
  setEmail: (val: string) => void
  password: string
  setPassword: (val: string) => void
  captchaToken: string | null
  setCaptchaToken: (val: string | null) => void
  recaptchaRef: React.RefObject<ReCAPTCHA | null>
  onSubmit: (e: React.FormEvent) => void
  onToggleLogin: () => void
  isLoading: boolean
  isPolling: boolean
  captchaEnabled: boolean
  captchaSiteKey: string
}

export const RegisterForm = ({
  fullName,
  setFullName,
  email,
  setEmail,
  password,
  setPassword,
  captchaToken,
  setCaptchaToken,
  recaptchaRef,
  onSubmit,
  onToggleLogin,
  isLoading,
  isPolling,
  captchaEnabled,
  captchaSiteKey
}: RegisterFormProps) => {
  return (
    <div>
      <form onSubmit={onSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="fullname">Teljes név</Label>
          <Input id="fullname" type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} required />
        </div>
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div className="space-y-2">
          <Label htmlFor="password">Jelszó</Label>
          <Input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        {captchaEnabled && (
          <div className="flex justify-center py-2">
            <ReCAPTCHA ref={recaptchaRef} sitekey={captchaSiteKey} onChange={(token) => setCaptchaToken(token)} />
          </div>
        )}
        <Button type="submit" className="w-full" disabled={isLoading || isPolling}>
          {isPolling ? 'Várakozás megerősítésre...' : 'Regisztráció'}
        </Button>
      </form>
      <div className="flex flex-col items-center space-y-2 mt-4">
        <Button variant="link" onClick={onToggleLogin} disabled={isPolling}>
          Van már fiókom
        </Button>
      </div>
    </div>
  )
}
