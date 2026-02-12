import { Alert, AlertIcon, Box, Button, FormControl, FormLabel, Heading, Input, Text, VStack } from '@chakra-ui/react'
import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Navigate, useNavigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTeamEdit } from '../../api/hooks/team/actions/useTeamEdit'
import { useTokenRefresh } from '../../api/hooks/useTokenRefresh.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths.ts'
import { RoleType, RoleTypeString } from '../../util/views/profile.view'
import { type TeamEditDto, TeamResponseMessages, TeamResponses } from '../../util/views/team.view'
import { FilePicker } from '../task/components/FilePicker'

export default function EditMyTeamPage() {
  const navigate = useNavigate()
  const [requestError, setRequestError] = useState<string>()
  const [logo, setLogo] = useState<File>()
  const brandColor = useBrandColor()
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
  const app = config?.components?.app
  const isPrivileged = RoleType[config?.role ?? RoleTypeString.GUEST] >= RoleType.PRIVILEGED
  if (!component || !isPrivileged) return <ComponentUnavailable />
  if (!component.teamEditEnabled) return <Navigate to="/" replace />

  return (
    <CmschPage>
      <title>
        {app?.siteName || 'CMSch'} | {component.teamEditTitle}
      </title>
      <Heading>{component.teamEditTitle}</Heading>
      <Markdown text={component.teamEditTopMessage} />
      <Alert status="info" my={5}>
        <AlertIcon />
        Az új bemutatkozás csak ellenőrzés után lesz látható.
      </Alert>
      <Box as="form" onSubmit={handleSubmit((dto) => teamEdit({ ...dto, logo }))}>
        <FormControl my={10}>
          <FormLabel>Bemutatkozás</FormLabel>
          <Input
            {...register('description', { required: true })}
            isInvalid={!!errors.description}
            placeholder="A legjobb csapat a világon (っ◕‿◕)っ"
          />
          {errors.description?.message && <Text color="red">{errors.description.message}</Text>}

          {component.teamLogoUploadEnabled && (
            <Box pt={4}>
              <FormLabel>Csapat logó</FormLabel>
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
          <Button isLoading={teamEditLoading} type="submit" colorScheme={brandColor}>
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
