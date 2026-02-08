import { Button, ButtonGroup, FormControl, FormLabel, Heading, Select, Text, VStack } from '@chakra-ui/react'
import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useGroupChangeMutation } from '../../api/hooks/group-change/useGroupChangeMutation'
import { useProfileQuery } from '../../api/hooks/profile/useProfileQuery.ts'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths'
import { GroupChangeDTO, GroupChangeStatus } from '../../util/views/groupChange.view'
import { ProfileView } from '../../util/views/profile.view.ts'

export function ProfileGroupChangePage() {
  const { isLoading, isError, data: profile, refetch } = useProfileQuery()

  if (isError || isLoading || !profile) return <PageStatus isLoading={isLoading} isError={isError} />
  if (!profile || !profile.groupSelectionAllowed) return <Navigate to={AbsolutePaths.PROFILE} />

  return <ProfileGroupChangeBody profile={profile} refetch={refetch} />
}

function ProfileGroupChangeBody({ profile, refetch }: { profile: ProfileView; refetch: () => void }) {
  const app = useConfigContext()?.components?.app
  const availableGroups = profile.availableGroups
    ? Object.entries<string>(profile.availableGroups).toSorted((a, b) => a[1].localeCompare(b[1]))
    : []

  const [value, setValue] = useState<string>()
  const [error, setError] = useState<string>()
  const { sendMessage } = useServiceContext()
  const navigate = useNavigate()
  const brandColor = useBrandColor()

  const onData = (response: GroupChangeDTO) => {
    switch (response.status) {
      case GroupChangeStatus.OK:
        refetch()
        sendMessage('Sikeres mentés!', { toast: true, toastStatus: 'success' })
        navigate(AbsolutePaths.PROFILE)
        break
      case GroupChangeStatus.INVALID_GROUP:
        setError('Érvénytelen tankör!')
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
    else setError('Válassz tankört!')
  }

  return (
    <CmschPage>
      <title>{app?.siteName || 'CMSch'} | Tankör beállítása</title>
      <Heading>Tankör beállítása</Heading>
      <Text mt={10} textAlign="center">
        Állítsd be a tankörödet, hogy részt vehess a feladatokban!
      </Text>
      <Text color="gray.500" textAlign="center">
        Csak helyesen beállított tankörrel fog érvényesülni a tanköri jelenlét!
      </Text>
      {availableGroups.length ? (
        <form>
          <VStack spacing={5} mt={10} maxW={80} mx="auto">
            <FormControl>
              <FormLabel>Melyik tankörbe tartozol?</FormLabel>
              <Select
                id="group"
                placeholder="Válassz tankört!"
                onChange={(evt) => {
                  setValue(evt.target.value)
                }}
              >
                {availableGroups?.map((entry) => (
                  <option key={entry[0]} value={entry[0]}>
                    {entry[1]}
                  </option>
                ))}
              </Select>
            </FormControl>
            {profile.fallbackGroup && (
              <ButtonGroup>
                <Button
                  variant="ghost"
                  colorScheme={brandColor}
                  onClick={() => {
                    setValue(profile.fallbackGroup?.toString() || '')
                  }}
                >
                  Vendég vagyok
                </Button>
              </ButtonGroup>
            )}
            <ButtonGroup>
              <Button onClick={onSubmit} colorScheme={brandColor} isLoading={isPending}>
                Mentés
              </Button>
              <LinkButton href={AbsolutePaths.PROFILE} colorScheme="red" variant="outline">
                Mégse
              </LinkButton>
            </ButtonGroup>
            {error && (
              <Text color="red.500" textAlign="center">
                {error}
              </Text>
            )}
          </VStack>
        </form>
      ) : (
        <Text textAlign="center" mt={4}>
          Nem találhatóak csoportok :(
        </Text>
      )}
    </CmschPage>
  )
}
