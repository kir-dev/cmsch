import {
  Alert,
  AlertDescription,
  AlertIcon,
  Button,
  FormControl,
  FormLabel,
  Heading,
  HStack,
  Input,
  Text,
  useToast,
  VStack
} from '@chakra-ui/react'
import { FormEvent, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { useNavigate } from 'react-router'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useAccessKeyMutation } from '../../api/hooks/access-key/useAccessKeyMutation'
import { useAccessKey } from '../../api/hooks/access-key/useAccessKeyQuery'
import { useTokenRefresh } from '../../api/hooks/useTokenRefresh.ts'
import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { AccessKeyResponse } from '../../util/views/accessKey'

function AccessKeyPage() {
  const { refetch } = useAuthContext()
  const tokenRefresh = useTokenRefresh()
  const [value, setValue] = useState<string>()
  const [error, setError] = useState<string>()
  const toast = useToast()
  const navigate = useNavigate()
  const brandColor = useBrandColor()

  const onData = async (response: AccessKeyResponse) => {
    if (response.success) {
      if (response.refreshSession) {
        await tokenRefresh.mutateAsync()
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
      <Heading as="h1" variant="main-title">
        {query.data.title}
      </Heading>

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
            <Input
              value={value}
              onChange={(e) => setValue(e.target.value)}
              isDisabled={!query.data.enabled}
              _placeholder={{ color: 'inherit' }}
            />
          </FormControl>
          <HStack>
            <Button type="submit" colorScheme={brandColor} isLoading={query.isLoading} isDisabled={!query.data.enabled}>
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
