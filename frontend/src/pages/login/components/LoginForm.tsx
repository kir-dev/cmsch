import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

interface LoginFormProps {
  email: string
  setEmail: (val: string) => void
  password: string
  setPassword: (val: string) => void
  onSubmit: (e: React.FormEvent) => void
  onToggleRegister: () => void
  onForgotPassword: () => void
  isLoading: boolean
  isPolling: boolean
  forgotPasswordEnabled: boolean
}

export const LoginForm = ({
  email,
  setEmail,
  password,
  setPassword,
  onSubmit,
  onToggleRegister,
  onForgotPassword,
  isLoading,
  isPolling,
  forgotPasswordEnabled
}: LoginFormProps) => {
  return (
    <div>
      <form onSubmit={onSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div className="space-y-2">
          <Label htmlFor="password">Jelszó</Label>
          <Input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <Button type="submit" className="w-full" disabled={isLoading || isPolling}>
          {isPolling ? 'Várakozás megerősítésre...' : 'Bejelentkezés'}
        </Button>
      </form>
      <div className="flex flex-col items-center space-y-2 mt-4">
        <Button variant="link" onClick={onToggleRegister} disabled={isPolling}>
          Nincs még fiókom, regisztrálok
        </Button>
        {forgotPasswordEnabled && (
          <Button variant="link" size="sm" onClick={onForgotPassword} disabled={isPolling}>
            Elfelejtettem a jelszavam
          </Button>
        )}
      </div>
    </div>
  )
}
