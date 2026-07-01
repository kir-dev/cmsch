import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { useAccessKeyMutation } from '@/api/hooks/access-key/useAccessKeyMutation'
import { useAccessKey } from '@/api/hooks/access-key/useAccessKeyQuery'
import { useTokenRefresh } from '@/api/hooks/useTokenRefresh.ts'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from '@/hooks/use-toast'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import type { AccessKeyResponse } from '@/util/views/accessKey'
import { AlertCircle } from 'lucide-react'
import { type FormEvent, useState } from 'react'
import { useNavigate } from 'react-router'

function AccessKeyPage() {
  const { refetch } = useAuthContext()
  const tokenRefresh = useTokenRefresh()
  const [value, setValue] = useState<string>('')
  const [error, setError] = useState<string>()
  const { toast } = useToast()
  const navigate = useNavigate()

  const onData = async (response: AccessKeyResponse) => {
    if (response.success) {
      if (response.refreshSession) {
        await tokenRefresh.mutateAsync()
        refetch()
      }
      toast({ title: l('access-token-success') })
      navigate(AbsolutePaths.PROFILE)
    } else {
      toast({ title: response.reason, variant: 'destructive' })
      setError(response.reason)
    }
  }

  const onError = () => setError(l('access-token-failed'))

  const mutation = useAccessKeyMutation(onData, onError)
  const query = useAccessKey()

  const onSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (value) {
      mutation.mutate({ key: value })
    } else {
      setError(l('access-token-missing'))
    }
  }

  if (query.isError || query.isLoading || !query.data) {
    return <PageStatus isLoading={query.isLoading} isError={query.isError} title="Azonosítás" />
  }

  return (
    <CmschPage title={query.data.title}>
      <h1 className="text-3xl font-bold font-heading">{query.data.title}</h1>

      {query.data.enabled ? (
        <div className="mt-5">
          <Markdown text={query.data.topMessage} />
        </div>
      ) : (
        <Alert variant="destructive" className="mt-5">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{l('access-token-not-available')}</AlertDescription>
        </Alert>
      )}
      <form onSubmit={onSubmit}>
        <div className="flex flex-col space-y-5 mt-10 items-start">
          <div className="grid w-full max-w-sm items-center gap-1.5">
            <Label htmlFor="access-key">{query.data.fieldName}</Label>
            <Input id="access-key" value={value} onChange={(e) => setValue(e.target.value)} disabled={!query.data.enabled} />
          </div>
          <div className="flex items-center space-x-4">
            <Button type="submit" disabled={query.isLoading || !query.data.enabled}>
              Beküldés
            </Button>
            {error && <p className="text-destructive text-center">{error}</p>}
          </div>
        </div>
      </form>
    </CmschPage>
  )
}

export default AccessKeyPage
