import { Button, FormControl, FormLabel, Heading, HStack, Input, Text, VStack } from '@chakra-ui/react'
import { useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { useForm } from 'react-hook-form'
import { Navigate, useNavigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTeamCreate } from '../../api/hooks/team/actions/useTeamCreate'
import { useTokenRefresh } from '../../api/hooks/useTokenRefresh.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { AbsolutePaths } from '../../util/paths.ts'
import { CreateTeamDto, TeamResponseMessages, TeamResponses } from '../../util/views/team.view'

export default function CreateTeamPage() {
  const navigate = useNavigate()
  const [requestError, setRequestError] = useState<string>()
  const config = useConfigContext()
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
          <Input
            {...register('name', { required: true })}
            isInvalid={!!errors.name}
            placeholder="Kedves csapatom"
            _placeholder={{ color: 'inherit' }}
          />
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
