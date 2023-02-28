import { useState } from 'react'
import { Button, ButtonGroup, FormControl, FormLabel, Heading, Select, Text, VStack } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { GroupChangeDTO, GroupChangeStatus } from '../../util/views/groupChange.view'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { useGroupChangeMutation } from '../../api/hooks/group-change/useGroupChangeMutation'

export function ProfileGroupChangePage() {
  const { profile, refetch } = useAuthContext()
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

  const { mutate, isLoading } = useGroupChangeMutation(onData, onError)

  const onSubmit = () => {
    if (value) mutate(value)
    else setError('Válassz tankört!')
  }

  if (!profile || !profile.groupSelectionAllowed) return <Navigate to={AbsolutePaths.PROFILE} />

  return (
    <CmschPage>
      <Helmet title="Tankör beállítása" />
      <Heading>Tankör beállítása</Heading>
      <Text mt={10} textAlign="center">
        Állítsd be a tankörödet, hogy részt vehess a feladatokban!
      </Text>
      <Text color="gray.500" textAlign="center">
        Csak helyesen beállított tankörrel fog érvényesülni a tanköri jelenlét!
      </Text>
      <form>
        <VStack spacing={5} mt={10} maxW={80} mx="auto">
          <FormControl>
            <FormLabel>Melyik tankörbe tartozol?</FormLabel>
            <Select
              id="group"
              onChange={(evt) => {
                setValue(evt.target.value)
              }}
            >
              {Object.entries<string>(profile.availableGroups)?.map((entry) => (
                <option key={entry[0]} value={entry[0]} selected={entry[1] === profile?.groupName}>
                  {entry[1]}
                </option>
              ))}
            </Select>
          </FormControl>
          <ButtonGroup>
            <Button
              variant="ghost"
              colorScheme="brand"
              onClick={() => {
                setValue(profile?.fallbackGroup.toString())
              }}
            >
              Vendég vagyok
            </Button>
          </ButtonGroup>
          <ButtonGroup>
            <Button onClick={onSubmit} colorScheme="brand" isLoading={isLoading}>
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
    </CmschPage>
  )
}
