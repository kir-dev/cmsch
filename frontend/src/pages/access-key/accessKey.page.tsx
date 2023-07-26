import { FormEvent, useState } from 'react'
import {
  Alert,
  AlertDescription,
  AlertIcon,
  Button,
  FormControl,
  FormLabel,
  HStack,
  Heading,
  Input,
  Text,
  VStack,
  useToast
} from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { useNavigate } from 'react-router-dom'
import { AbsolutePaths } from '../../util/paths'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { useAccessKeyMutation } from '../../api/hooks/access-key/useAccessKeyMutation'
import { useAccessKey } from '../../api/hooks/access-key/useAccessKeyQuery'
import { PageStatus } from '../../common-components/PageStatus'
import Markdown from '../../common-components/Markdown'
import { AccessKeyResponse } from '../../util/views/accessKey'
import { l } from '../../util/language'

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
      toast({ title: l('access-token-success'), status: 'success' })
      navigate(AbsolutePaths.PROFILE)
    } else {
      toast({ title: response.reason, status: 'error' })
      setError(response.reason)
    }
  }

  const onError = () => setError(l('access-token-failed'))

  const mutation = useAccessKeyMutation(onData, onError)
  const query = useAccessKey()

  const onSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (value) {
      mutation.mutate({ key: value })
    } else {
      setError(l('access-token-missing'))
    }
  }

  if (query.isError || query.isLoading || !query.data) {
    return <PageStatus isLoading={query.isLoading} isError={query.isError} title="Azonosítás" />
  }

  return (
    <CmschPage>
      <Helmet title={query.data.title} />
      <Heading>{query.data.title}</Heading>

      {query.data.enabled ? (
        <Markdown text={query.data.topMessage} />
      ) : (
        <Alert status="error">
          <AlertIcon />
          <AlertDescription>{l('access-token-not-available')}</AlertDescription>
        </Alert>
      )}
      <form onSubmit={onSubmit}>
        <VStack spacing={5} mt={10} alignItems="flex-start">
          <FormControl>
            <FormLabel>{query.data.fieldName}</FormLabel>
            <Input value={value} onChange={(e) => setValue(e.target.value)} isDisabled={!query.data.enabled} />
          </FormControl>
          <HStack>
            <Button type="submit" colorScheme="brand" isLoading={query.isLoading} isDisabled={!query.data.enabled}>
              Beküldés
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
