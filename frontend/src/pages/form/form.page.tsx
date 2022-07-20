import { FunctionComponent } from 'react'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Box, Heading } from '@chakra-ui/react'
import { Navigate, useParams } from 'react-router-dom'
import { Helmet } from 'react-helmet'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { Loading } from '../../common-components/Loading'
import { AbsolutePaths } from '../../util/paths'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useFormPage } from '../../api/hooks/useFormPage'
import { AutoFormField } from './components/autoFormField'

interface FormPageProps {}

const FormPage: FunctionComponent<FormPageProps> = () => {
  const params = useParams()
  const { profile } = useAuthContext()
  const { data, isLoading, error } = useFormPage(params.slug || '')
  const { sendMessage } = useServiceContext()

  if (isLoading) {
    return <Loading />
  }

  if (error) {
    sendMessage('Űrlap betöltése sikertelen!')
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof data === 'undefined') {
    sendMessage('Űrlap betöltése sikertelen!\n Keresd az oldal fejlesztőit!')
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
  const {
    form: { formJson, name, url, availableFrom, availableUntil },
    status
  } = data
  return (
    <CmschPage>
      <Helmet title={name} />
      <Box w="30rem" maxW="100%">
        <Heading>{name}</Heading>
        {formJson.map((formField) => (
          <AutoFormField key={formField.fieldName} field={formField} />
        ))}
      </Box>
    </CmschPage>
  )
}

export default FormPage
