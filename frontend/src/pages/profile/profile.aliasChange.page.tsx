import { Box, Button, FormControl, FormLabel, Heading, HStack, Input, useColorModeValue, useToast } from '@chakra-ui/react'
import { FormEvent, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate } from 'react-router-dom'

import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useAliasChangeMutation } from '../../api/hooks/alias/useAliasChangeMutation'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { PageStatus } from '../../common-components/PageStatus'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

export const AliasChangePage = () => {
  const navigate = useNavigate()
  const toast = useToast()
  const submissionMutation = useAliasChangeMutation()
  const borderColor = useColorModeValue('gray.200', 'gray.600')
  const { profile, profileLoading, profileError } = useAuthContext()
  const [alias, setAlias] = useState<string>(profile?.alias || '')
  const component = useConfigContext()?.components.profile

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
      <Box as="form" borderWidth={2} borderColor={borderColor} borderRadius="md" p={5} mt={5} onSubmit={onSubmitAlias}>
        <FormControl>
          <FormLabel htmlFor="alias">Becenév:</FormLabel>
          <Input
            id="alias"
            name="alias"
            autoComplete="off"
            placeholder="Becenév"
            value={alias}
            onChange={(e) => setAlias(e.target.value)}
          />
        </FormControl>

        <HStack spacing={3} mt={10}>
          <Button type="submit" colorScheme="brand" width="100%">
            Mentés
          </Button>
          <Button type="button" onClick={removeAlias} colorScheme="red" width="100%">
            Törlés
          </Button>
          <Button type="button" onClick={() => navigate(AbsolutePaths.PROFILE)} colorScheme="gray" width="100%">
            Mégse
          </Button>
        </HStack>
      </Box>
    </CmschPage>
  )
}
