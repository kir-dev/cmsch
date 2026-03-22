import { useResetPasswordMutation } from '@/api/hooks/auth/useResetPasswordMutation'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from '@/hooks/use-toast'
import { useState } from 'react'
import { Navigate, useSearchParams } from 'react-router'

const ResetPasswordPage = () => {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')
  const { toast } = useToast()

  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [isDone, setIsDone] = useState(false)

  const resetPasswordMutation = useResetPasswordMutation()

  if (!token) {
    return <Navigate to="/login" />
  }

  if (isDone) {
    return <Navigate to="/login" />
  }

  const handleReset = (e: React.FormEvent) => {
    e.preventDefault()
    if (password !== confirmPassword) {
      toast({ title: 'Hiba', description: 'A jelszavak nem egyeznek', variant: 'destructive' })
      return
    }

    resetPasswordMutation.mutate(
      { token, newPassword: password },
      {
        onSuccess: (data) => {
          if (data.status === 'OK') {
            toast({ title: 'Sikeres jelszó visszaállítás', description: 'Most már bejelentkezhetsz az új jelszavaddal' })
            setIsDone(true)
          } else {
            toast({ title: 'Hiba', description: data.message, variant: 'destructive' })
          }
        },
        onError: (error) => {
          console.error(error)
          window.processAndReportError(error)
          toast({ title: 'Hiba', description: 'Hálózati hiba történt', variant: 'destructive' })
        }
      }
    )
  }

  return (
    <CmschPage title="Jelszó visszaállítása">
      <div className="flex flex-col items-center space-y-10 mb-10">
        <Card className="w-full max-w-md shadow-lg mt-10">
          <CardHeader>
            <CardTitle className="text-center text-xl">Új jelszó megadása</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleReset} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="password">Új jelszó</Label>
                <Input id="password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="confirm-password">Új jelszó megerősítése</Label>
                <Input
                  id="confirm-password"
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                />
              </div>
              <Button type="submit" className="w-full bg-primary text-primary-foreground" disabled={resetPasswordMutation.isPending}>
                Jelszó mentése
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </CmschPage>
  )
}

export default ResetPasswordPage
