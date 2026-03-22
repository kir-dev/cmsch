import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useAliasChangeMutation } from '@/api/hooks/alias/useAliasChangeMutation'
import { useProfileQuery } from '@/api/hooks/profile/useProfileQuery.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { PageStatus } from '@/common-components/PageStatus'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from '@/hooks/use-toast'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import { type FormEvent, useState } from 'react'
import { Navigate, useNavigate } from 'react-router'

export const AliasChangePage = () => {
  const navigate = useNavigate()
  const { toast } = useToast()
  const submissionMutation = useAliasChangeMutation()
  const { isLoading: profileLoading, data: profile, error: profileError } = useProfileQuery()
  const [alias, setAlias] = useState<string>(profile?.alias || '')
  const component = useConfigContext()?.components?.profile

  if (!component) return <ComponentUnavailable />

  if (profileError || profileLoading || !profile) return <PageStatus isLoading={profileLoading} isError={!!profileError} />

  if (!component.aliasChangeEnabled) {
    toast({ title: l('alias-change-not-allowed'), variant: 'destructive' })
    return <Navigate to={AbsolutePaths.PROFILE} />
  }

  const onSubmitAlias = (e: FormEvent) => {
    e.preventDefault()
    submissionMutation.mutate(alias, {
      onSuccess: (result) => {
        if (result) {
          toast({ title: l('alias-change-successful') })
          navigate(AbsolutePaths.PROFILE)
        } else {
          toast({ title: l('alias-change-failure'), variant: 'destructive' })
        }
      },
      onError: (err) => {
        toast({ title: l('alias-change-failure'), variant: 'destructive', description: err.message })
      }
    })
  }

  const removeAlias = () => {
    submissionMutation.mutate('', {
      onSuccess: (result) => {
        if (result) {
          toast({ title: l('alias-change-successful') })
          navigate(AbsolutePaths.PROFILE)
        } else {
          toast({ title: l('alias-change-failure'), variant: 'destructive' })
        }
      },
      onError: (err) => {
        toast({ title: l('alias-change-failure'), variant: 'destructive', description: err.message })
      }
    })
  }

  return (
    <CmschPage title="Becenév módosítása">
      <h1 className="my-5 text-4xl font-bold tracking-tight">Becenév módosítása</h1>
      <form onSubmit={onSubmitAlias} className="flex flex-col gap-8">
        <div className="flex flex-col gap-2">
          <Label htmlFor="alias">Add meg a beceneved:</Label>
          <Input
            id="alias"
            name="alias"
            autoComplete="off"
            placeholder="Becenév"
            value={alias}
            onChange={(e) => setAlias(e.target.value)}
          />
        </div>
        <div className="flex gap-3 justify-center">
          <Button type="submit">Mentés</Button>
          <Button type="button" onClick={removeAlias} variant="destructive">
            Törlés
          </Button>
        </div>
      </form>
    </CmschPage>
  )
}
