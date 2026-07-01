import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import type { Profile } from '@/api/contexts/config/types.ts'
import { useServiceContext } from '@/api/contexts/service/ServiceContext'
import { useGroupChangeMutation } from '@/api/hooks/group-change/useGroupChangeMutation'
import { useProfileQuery } from '@/api/hooks/profile/useProfileQuery.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import { PageStatus } from '@/common-components/PageStatus.tsx'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { AbsolutePaths } from '@/util/paths'
import { type GroupChangeDTO, GroupChangeStatus } from '@/util/views/groupChange.view'
import type { ProfileView } from '@/util/views/profile.view.ts'
import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router'

export function ProfileGroupChangePage() {
  const { isLoading, isError, data: profile, refetch } = useProfileQuery()

  const config = useConfigContext()?.components
  const component = config?.profile

  if (!component) return <ComponentUnavailable />
  if (isError || isLoading || !profile) return <PageStatus isLoading={isLoading} isError={isError} />
  if (!profile || !profile.groupSelectionAllowed) return <Navigate to={AbsolutePaths.PROFILE} />

  return <ProfileGroupChangeBody profile={profile} profileComponent={component} refetch={refetch} />
}

function ProfileGroupChangeBody({
  profile,
  profileComponent,
  refetch
}: {
  profile: ProfileView
  profileComponent: Profile
  refetch: () => void
}) {
  const availableGroups = profile.availableGroups
    ? Object.entries<string>(profile.availableGroups).toSorted((a, b) => a[1].localeCompare(b[1]))
    : []

  const [value, setValue] = useState<string>()
  const [error, setError] = useState<string>()
  const { sendMessage } = useServiceContext()
  const navigate = useNavigate()

  const onData = (response: GroupChangeDTO) => {
    switch (response.status) {
      case GroupChangeStatus.OK:
        refetch()
        sendMessage('Sikeres mentés!', { toast: true, toastStatus: 'success' })
        navigate(AbsolutePaths.PROFILE)
        break
      case GroupChangeStatus.INVALID_GROUP:
        setError(`Érvénytelen ${profileComponent.groupTitle}!`)
        break
      case GroupChangeStatus.LEAVE_DENIED:
        setError('Nem engedélyezett a módosítás!')
        break
      case GroupChangeStatus.UNAUTHORIZED:
        setError('Nem engedélyezett!')
        break
      default:
        setError('Valami nem stimmel, nem sikerült a módosítás.')
        break
    }
  }

  const onError = () => setError('Nem sikerült a módosítás!')

  const { mutate, isPending } = useGroupChangeMutation(onData, onError)

  const onSubmit = () => {
    if (value) mutate(value)
    else setError(`Válassz ${profileComponent.groupTitle}-t!`)
  }

  return (
    <CmschPage title={`${profileComponent.groupTitle} beállítása`}>
      <h1 className="text-4xl font-bold tracking-tight">{`${profileComponent.groupTitle} beállítása`}</h1>
      <p className="mt-10 text-center">Állíts be saját {profileComponent.groupTitle}-t, hogy részt vehess a feladatokban!</p>
      {profileComponent.showMinimumTokenMessage && (
        <p className="text-center text-muted-foreground">Csak helyesen beállított tankör esetén fog érvényesülni a tanköri jelenlét!</p>
      )}
      {availableGroups.length ? (
        <form
          onSubmit={(e) => {
            e.preventDefault()
            onSubmit()
          }}
        >
          <div className="mx-auto mt-10 flex max-w-80 flex-col gap-5">
            <div className="flex flex-col gap-2">
              <Label htmlFor="group">Melyik {profileComponent.groupTitle}-be tartozol?</Label>
              <Select value={value} onValueChange={setValue}>
                <SelectTrigger id="group">
                  <SelectValue placeholder={`Válassz ${profileComponent.groupTitle}-t!`} />
                </SelectTrigger>
                <SelectContent>
                  {availableGroups?.map((entry) => (
                    <SelectItem key={entry[0]} value={entry[0]}>
                      {entry[1]}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            {profile.fallbackGroup && (
              <div className="flex justify-center">
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => {
                    setValue(profile.fallbackGroup?.toString() || '')
                  }}
                >
                  Vendég vagyok
                </Button>
              </div>
            )}
            <div className="flex justify-center gap-3">
              <Button disabled={isPending} type="submit">
                {isPending ? 'Mentés...' : 'Mentés'}
              </Button>
              <LinkButton href={AbsolutePaths.PROFILE} variant="outline" type="button">
                Mégse
              </LinkButton>
            </div>
            {error && <p className="text-center text-destructive">{error}</p>}
          </div>
        </form>
      ) : (
        <p className="mt-4 text-center">Nem találhatóak csoportok :(</p>
      )}
    </CmschPage>
  )
}
