import { FormEvent, useState } from 'react'
import { Alert, AlertDescription, Button, Fieldset, Heading, HStack, Input, Text, VStack } from '@chakra-ui/react'
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
import { toaster } from '../../components/ui/toaster'
import { CiCircleAlert } from 'react-icons/ci'
import { Field } from '../../components/ui/field.tsx'

function AccessKeyPage() {
  const { refetch } = useAuthContext()
  const [value, setValue] = useState<string>()
  const [error, setError] = useState<string>()
  const navigate = useNavigate()

  const onData = (response: AccessKeyResponse) => {
    if (response.success) {
      if (response.refreshSession) {
        refetch()
      }
      toaster.create({ title: l('access-token-success'), type: 'success' })
      navigate(AbsolutePaths.PROFILE)
    } else {
      toaster.create({ title: response.reason, type: 'error' })
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
      <Heading as="h1">{query.data.title}</Heading>

      {query.data.enabled ? (
        <Markdown text={query.data.topMessage} />
      ) : (
        <Alert.Root status="error">
          <Alert.Content>
            <CiCircleAlert />
            <AlertDescription>{l('access-token-not-available')}</AlertDescription>\
          </Alert.Content>
        </Alert.Root>
      )}
      <Fieldset.Root>
        <form onSubmit={onSubmit}>
          <VStack gap={5} mt={10} alignItems="flex-start">
            <Fieldset.Content>
              <Field label={query.data.fieldName}>
                <Input value={value} onChange={(e) => setValue(e.target.value)} disabled={!query.data.enabled} />
              </Field>
              <HStack>
                <Button type="submit" colorScheme="brand" loading={query.isLoading} disabled={!query.data.enabled}>
                  Beküldés
                </Button>
                {error && (
                  <Text color="red.500" textAlign="center">
                    {error}
                  </Text>
                )}
              </HStack>
            </Fieldset.Content>
          </VStack>
        </form>
      </Fieldset.Root>
    </CmschPage>
  )
}

export default AccessKeyPage
