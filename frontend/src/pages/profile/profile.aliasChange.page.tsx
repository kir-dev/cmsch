import { Box, Button, ButtonGroup, FormControl, FormLabel, Heading, Input, useToast } from '@chakra-ui/react'
import { FormEvent, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useAliasChangeMutation } from '../../api/hooks/alias/useAliasChangeMutation'
import { useProfileQuery } from '../../api/hooks/profile/useProfileQuery.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'

export const AliasChangePage = () => {
  const navigate = useNavigate()
  const toast = useToast()
  const submissionMutation = useAliasChangeMutation()
  const { isLoading: profileLoading, data: profile, error: profileError } = useProfileQuery()
  const [alias, setAlias] = useState<string>(profile?.alias || '')
  const component = useConfigContext()?.components?.profile

  if (!component) return <ComponentUnavailable />

  if (profileError || profileLoading || !profile) return <PageStatus isLoading={profileLoading} isError={!!profileError} />

  if (!component.aliasChangeEnabled) {
    toast({ title: l('alias-change-not-allowed'), status: 'error' })
    return <Navigate to={AbsolutePaths.PROFILE} />
  }

  const onSubmitAlias = (e: FormEvent) => {
    e.preventDefault()
    submissionMutation.mutate(alias, {
      onSuccess: (result) => {
        if (result) {
          toast({ title: l('alias-change-successful'), status: 'success' })
          navigate(AbsolutePaths.PROFILE)
        } else {
          toast({ title: l('alias-change-failure'), status: 'error' })
        }
      },
      onError: (err) => {
        toast({ title: l('alias-change-failure'), status: 'error', description: err.message })
      }
    })
  }

  const removeAlias = () => {
    submissionMutation.mutate('', {
      onSuccess: (result) => {
        if (result) {
          toast({ title: l('alias-change-successful'), status: 'success' })
          navigate(AbsolutePaths.PROFILE)
        } else {
          toast({ title: l('alias-change-failure'), status: 'error' })
        }
      },
      onError: (err) => {
        toast({ title: l('alias-change-failure'), status: 'error', description: err.message })
      }
    })
  }

  return (
    <CmschPage>
      <Helmet title="Becenév módosítása" />
      <Heading my={5}>Becenév módosítása</Heading>
      <Box display="flex" flexDirection="column" as="form" onSubmit={onSubmitAlias}>
        <FormControl>
          <FormLabel htmlFor="alias">Add meg a beceneved:</FormLabel>
          <Input
            id="alias"
            name="alias"
            autoComplete="off"
            placeholder="Becenév"
            value={alias}
            onChange={(e) => setAlias(e.target.value)}
          />
        </FormControl>
        <ButtonGroup mt={8} spacing={3} alignSelf="center">
          <Button type="submit" colorScheme="brand">
            Mentés
          </Button>
          <Button type="button" onClick={removeAlias} colorScheme="red" variant="outline">
            Törlés
          </Button>
        </ButtonGroup>
      </Box>
    </CmschPage>
  )
}
