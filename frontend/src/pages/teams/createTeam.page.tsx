import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet-async'
import { useForm } from 'react-hook-form'
import { CreateTeamDto, TeamResponseMessages, TeamResponses } from '../../util/views/team.view'
import { Button, FormControl, FormLabel, Heading, HStack, Input, Text, VStack } from '@chakra-ui/react'
import { useTeamCreate } from '../../api/hooks/team/actions/useTeamCreate'
import { Navigate, useNavigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { useState } from 'react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import Markdown from '../../common-components/Markdown'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

export default function CreateTeamPage() {
  const navigate = useNavigate()
  const [requestError, setRequestError] = useState<string>()
  const { refreshToken } = useAuthContext()
  const config = useConfigContext()
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<CreateTeamDto>()

  const { createTeamLoading, createTeamError, createTeam } = useTeamCreate((response) => {
    if (response === TeamResponses.OK) {
      refreshToken(() => navigate(AbsolutePaths.MY_TEAM))
    } else {
      setRequestError(TeamResponseMessages[response as TeamResponses])
    }
  })
  const component = config?.components.team
  if (!component) return <ComponentUnavailable />
  if (!component.creationEnabled) return <Navigate to="/" replace />

  return (
    <CmschPage>
      <Helmet title={component.createTitle} />
      <Heading>{component.createTitle}</Heading>
      <Markdown text={component.teamCreationTopMessage} />
      <form onSubmit={handleSubmit(createTeam)}>
        <FormControl my={10}>
          <FormLabel>Név</FormLabel>
          <Input {...register('name', { required: true })} isInvalid={!!errors.name} placeholder="Kedves csapatom" />
          {errors.name?.message && <Text color="red">{errors.name.message}</Text>}
        </FormControl>
        <HStack>
          <Button isLoading={createTeamLoading} type="submit" colorScheme="brand">
            Létrehozom!
          </Button>
          <VStack>
            {requestError && <Text color="red">{requestError}</Text>}
            {createTeamError && <Text color="red">Hiba történt a csoport létrehozása közben!</Text>}
          </VStack>
        </HStack>
      </form>
    </CmschPage>
  )
}
