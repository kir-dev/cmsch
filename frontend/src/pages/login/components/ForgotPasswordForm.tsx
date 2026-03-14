import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

interface ForgotPasswordFormProps {
  email: string
  setEmail: (val: string) => void
  onSubmit: (e: React.FormEvent) => void
  onBack: () => void
  isLoading: boolean
}

export const ForgotPasswordForm = ({ email, setEmail, onSubmit, onBack, isLoading }: ForgotPasswordFormProps) => {
  return (
    <div className="space-y-4">
      <form onSubmit={onSubmit}>
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <Button type="submit" className="w-full mt-8" disabled={isLoading}>
          Visszaállítás kérése
        </Button>
      </form>
      <Button variant="link" className="w-full mt-4" onClick={onBack}>
        Vissza a bejelentkezéshez
      </Button>
    </div>
  )
}
