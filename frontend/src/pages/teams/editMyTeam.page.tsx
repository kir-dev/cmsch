import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet-async'
import { useForm } from 'react-hook-form'
import { TeamEditDto, TeamResponseMessages, TeamResponses } from '../../util/views/team.view'
import { Box, Button, FormControl, FormLabel, Heading, Input, Text, VStack } from '@chakra-ui/react'
import { Navigate, useNavigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { useState } from 'react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import Markdown from '../../common-components/Markdown'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { useTeamEdit } from '../../api/hooks/team/actions/useTeamEdit'
import { FilePicker } from '../task/components/FilePicker'
import { RoleType } from '../../util/views/profile.view'

export default function EditMyTeamPage() {
  const navigate = useNavigate()
  const [requestError, setRequestError] = useState<string>()
  const [logo, setLogo] = useState<File>()
  const { refreshToken } = useAuthContext()
  const config = useConfigContext()
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<TeamEditDto>()

  const { teamEditLoading, teamEditError, teamEdit } = useTeamEdit((response) => {
    if (response === TeamResponses.OK) {
      refreshToken(() => navigate(AbsolutePaths.MY_TEAM))
    } else {
      setRequestError(TeamResponseMessages[response as TeamResponses])
    }
  })
  const component = config?.components.team
  const isPrivileged = RoleType[config?.role ?? 0] >= RoleType.PRIVILEGED
  if (!component || !isPrivileged) return <ComponentUnavailable />
  if (!component.teamEditEnabled) return <Navigate to="/" replace />

  return (
    <CmschPage>
      <Helmet title={component.teamEditTitle} />
      <Heading>{component.teamEditTitle}</Heading>
      <Markdown text={component.teamEditTopMessage} />
      <Box as="form" onSubmit={handleSubmit((dto) => teamEdit({ ...dto, logo }))}>
        <FormControl my={10}>
          <FormLabel>Csapat leírása</FormLabel>
          <Input
            {...register('description', { required: true })}
            isInvalid={!!errors.description}
            placeholder="A legjobb csapat a világon (っ◕‿◕)っ"
          />
          {errors.description?.message && <Text color="red">{errors.description.message}</Text>}

          {component.teamLogoUploadEnabled && (
            <Box pt={4}>
              <FormLabel>Csapat Logó</FormLabel>
              <FilePicker
                onFileChange={(files) => setLogo(files[0])}
                placeholder="Csapat logó"
                clearButtonLabel="Törlés"
                accept={'image/jpeg,image/png,image/jpg,image/gif'}
              />
            </Box>
          )}
        </FormControl>
        <VStack alignItems="start">
          <Button isLoading={teamEditLoading} type="submit" colorScheme="brand">
            Mentés
          </Button>
          <VStack alignItems="start">
            {requestError && <Text color="red">{requestError}</Text>}
            {teamEditError && <Text color="red">Hiba történt a csoport adatainak szerkesztése közben!</Text>}
          </VStack>
        </VStack>
      </Box>
    </CmschPage>
  )
}
