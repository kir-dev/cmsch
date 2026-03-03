import { CmschPage } from '@/common-components/layout/CmschPage'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from '@/hooks/use-toast'
import { API_BASE_URL } from '@/util/configs/environment.config'
import { ApiPaths } from '@/util/paths'
import axios from 'axios'
import { useState } from 'react'
import { Navigate, useSearchParams } from 'react-router'

const ResetPasswordPage = () => {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')
  const { toast } = useToast()

  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [isDone, setIsDone] = useState(false)

  if (!token) {
    return <Navigate to="/login" />
  }

  if (isDone) {
    return <Navigate to="/login" />
  }

  const handleReset = async (e: React.FormEvent) => {
    e.preventDefault()
    if (password !== confirmPassword) {
      toast({ title: 'Hiba', description: 'A jelszavak nem egyeznek', variant: 'destructive' })
      return
    }
    setIsLoading(true)
    try {
      const response = await axios.post(`${API_BASE_URL}${ApiPaths.RESET_PASSWORD}`, { token, newPassword: password })
      if (response.data.status === 'ok') {
        toast({ title: 'Sikeres jelszó visszaállítás', description: 'Most már bejelentkezhetsz az új jelszavaddal' })
        setIsDone(true)
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
              <Button type="submit" className="w-full bg-primary text-primary-foreground" disabled={isLoading}>
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
