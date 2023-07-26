import { useState } from 'react'
import { Button, ButtonGroup, FormControl, FormLabel, HStack, Heading, Input, Select, Text, VStack, useToast } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate } from 'react-router-dom'
import { AbsolutePaths, Paths } from '../../util/paths'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { GroupChangeDTO, GroupChangeStatus } from '../../util/views/groupChange.view'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import { useGroupChangeMutation } from '../../api/hooks/group-change/useGroupChangeMutation'
import { useAccessKeyMutation } from '../../api/hooks/access-key/useAccessKeyMutation'
import { useAccessKey } from '../../api/hooks/access-key/useAccessKeyQuery'
import { PageStatus } from '../../common-components/PageStatus'
import Markdown from '../../common-components/Markdown'
import { AccessKeyResponse } from '../../util/views/accessKey'

function AccessKeyPage() {
  const { refetch } = useAuthContext()
  const [value, setValue] = useState<string>()
  const [error, setError] = useState<string>()
  const toast = useToast()
  const navigate = useNavigate()

  const onData = (response: AccessKeyResponse) => {
    if (response.success) {
      if (response.refreshSession) {
        refetch()
      }
      toast({ title: 'Sikeres azonosítás!', status: 'success' })
      navigate(AbsolutePaths.PROFILE)
    } else {
      toast({ title: response.reason, status: 'error' })
      setError(response.reason)
    }
  }

  const onError = () => setError('Nem sikerült az azonosítás!')

  const mutation = useAccessKeyMutation(onData, onError)
  const query = useAccessKey()

  const onSubmit = () => {
    if (value) {
      mutation.mutate({ key: value })
    } else {
      setError('Add meg a kódot!')
    }
  }

  if (query.isError || query.isLoading || !query.data) {
    return <PageStatus isLoading={query.isLoading} isError={query.isError} title="Azonosítás" />
  }

  return (
    <CmschPage>
      <Helmet title={query.data.title} />
      <Heading>{query.data.title}</Heading>
      <Markdown text={query.data.topMessage} />
      <form>
        <VStack spacing={5} mt={10} alignItems="flex-start">
          <FormControl>
            <FormLabel>{query.data.fieldName}</FormLabel>
            <Input value={value} onChange={(e) => setValue(e.target.value)} />
          </FormControl>
          <HStack>
            <Button onClick={onSubmit} colorScheme="brand" isLoading={query.isLoading} isDisabled={!query.data.enabled}>
              Mentés
            </Button>
            {error && (
              <Text color="red.500" textAlign="center">
                {error}
              </Text>
            )}
          </HStack>
        </VStack>
      </form>
    </CmschPage>
  )
}

export default AccessKeyPage
