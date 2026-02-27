import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTeamCreate } from '@/api/hooks/team/actions/useTeamCreate'
import { useTokenRefresh } from '@/api/hooks/useTokenRefresh.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { AbsolutePaths } from '@/util/paths.ts'
import { type CreateTeamDto, TeamResponseMessages, TeamResponses } from '@/util/views/team.view'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Navigate, useNavigate } from 'react-router'

export default function CreateTeamPage() {
  const navigate = useNavigate()
  const [requestError, setRequestError] = useState<string>()
  const tokenRefresh = useTokenRefresh(() => {
    navigate(AbsolutePaths.MY_TEAM)
  })
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<CreateTeamDto>()

  const { createTeamLoading, createTeamError, createTeam } = useTeamCreate((response) => {
    if (response === TeamResponses.OK) {
      tokenRefresh.mutate()
    } else {
      setRequestError(TeamResponseMessages[response as TeamResponses])
    }
  })
  const component = useConfigContext()?.components?.team
  if (!component) return <ComponentUnavailable />
  if (!component.creationEnabled) return <Navigate to="/" replace />

  return (
    <CmschPage title={component.createTitle}>
      <h1 className="text-4xl font-bold tracking-tight mb-5">{component.createTitle}</h1>
      <Markdown text={component.teamCreationTopMessage} />
      <form onSubmit={handleSubmit(createTeam)}>
        <div className="my-10 flex flex-col gap-2">
          <Label htmlFor="name">Név</Label>
          <Input
            id="name"
            {...register('name', { required: true })}
            className={errors.name ? 'border-destructive' : ''}
            placeholder="Kedves csapatom"
          />
          {errors.name?.message && <p className="text-destructive">{errors.name.message}</p>}
        </div>
        <div className="flex items-center gap-4">
          <Button disabled={createTeamLoading} type="submit">
            {createTeamLoading ? 'Létrehozás...' : 'Létrehozom!'}
          </Button>
          <div className="flex flex-col">
            {requestError && <p className="text-destructive">{requestError}</p>}
            {createTeamError && <p className="text-destructive">Hiba történt a csoport létrehozása közben!</p>}
          </div>
        </div>
      </form>
    </CmschPage>
  )
}
