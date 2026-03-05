import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTeamEdit } from '@/api/hooks/team/actions/useTeamEdit'
import { useTokenRefresh } from '@/api/hooks/useTokenRefresh.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { FilePicker } from '@/pages/task/components/FilePicker'
import { AbsolutePaths } from '@/util/paths.ts'
import { RoleType, RoleTypeString } from '@/util/views/profile.view'
import { type TeamEditDto, TeamResponseMessages, TeamResponses } from '@/util/views/team.view'
import { Info } from 'lucide-react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Navigate, useNavigate } from 'react-router'

export default function EditMyTeamPage() {
  const navigate = useNavigate()
  const [requestError, setRequestError] = useState<string>()
  const [logo, setLogo] = useState<File>()
  const tokenRefresh = useTokenRefresh(() => {
    navigate(AbsolutePaths.MY_TEAM)
  })
  const config = useConfigContext()
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<TeamEditDto>()

  const { teamEditLoading, teamEditError, teamEdit } = useTeamEdit((response) => {
    if (response === TeamResponses.OK) {
      tokenRefresh.mutate()
    } else {
      setRequestError(TeamResponseMessages[response as TeamResponses])
    }
  })
  const component = config?.components?.team
  const isPrivileged = RoleType[config?.role ?? RoleTypeString.GUEST] >= RoleType.PRIVILEGED
  if (!component || !isPrivileged) return <ComponentUnavailable />
  if (!component.teamEditEnabled) return <Navigate to="/" replace />

  return (
    <CmschPage title={component.teamEditTitle}>
      <h1 className="text-4xl font-bold tracking-tight mb-5">{component.teamEditTitle}</h1>
      <Markdown text={component.teamEditTopMessage} />
      <Alert className="my-5 border-l-4">
        <Info className="h-4 w-4" />
        <AlertDescription>Az új bemutatkozás csak ellenőrzés után lesz látható.</AlertDescription>
      </Alert>
      <form onSubmit={handleSubmit((dto) => teamEdit({ ...dto, logo }))}>
        <div className="my-10 flex flex-col gap-5">
          <div className="flex flex-col gap-2">
            <Label htmlFor="description">Bemutatkozás</Label>
            <Input
              id="description"
              {...register('description', { required: true })}
              className={errors.description ? 'border-destructive' : ''}
              placeholder="A legjobb csapat a világon (っ◕‿◕)っ"
            />
            {errors.description?.message && <p className="text-destructive">{errors.description.message}</p>}
          </div>

          {component.teamLogoUploadEnabled && (
            <div className="pt-4 flex flex-col gap-2">
              <Label>Csapat logó</Label>
              <FilePicker
                onFileChange={(files) => setLogo(files[0])}
                placeholder="Csapat logó"
                clearButtonLabel="Törlés"
                accept={'image/jpeg,image/png,image/jpg,image/gif'}
              />
            </div>
          )}
        </div>
        <div className="flex flex-col items-start gap-4">
          <Button disabled={teamEditLoading} type="submit">
            {teamEditLoading ? 'Mentés...' : 'Mentés'}
          </Button>
          <div className="flex flex-col items-start">
            {requestError && <p className="text-destructive">{requestError}</p>}
            {teamEditError && <p className="text-destructive">Hiba történt a csoport adatainak szerkesztése közben!</p>}
          </div>
        </div>
      </form>
    </CmschPage>
  )
}
