import { FunctionComponent } from 'react'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Box, Button, FormControl, FormLabel, Heading } from '@chakra-ui/react'
import { Navigate, useParams } from 'react-router-dom'
import { Helmet } from 'react-helmet'
import { Loading } from '../../common-components/Loading'
import { AbsolutePaths } from '../../util/paths'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useFormPage } from '../../api/hooks/useFormPage'
import { AutoFormField } from './components/autoFormField'
import { useForm } from 'react-hook-form'
import Markdown from '../../common-components/Markdown'
import { useFormSubmit } from '../../api/hooks/useFormSubmit'
import { useQueryClient } from 'react-query'
import { FormStatusBadge } from './components/formStatusBadge'
import { FormStatus } from '../../util/views/form.view'

interface FormPageProps {}

const FormPage: FunctionComponent<FormPageProps> = () => {
  const params = useParams()
  const { submit, submitLoading } = useFormSubmit(params.slug || '')
  const { data, isLoading, error } = useFormPage(params.slug || '')
  const { invalidateQueries } = useQueryClient()
  const { sendMessage } = useServiceContext()
  const { control, handleSubmit } = useForm()

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
    form: { formFields, name, availableFrom, availableUntil },
    status
  } = data
  const available = availableFrom * 1000 < Date.now() && availableUntil * 1000 > Date.now()
  const onSubmit = (values: Record<string, unknown>) => {
    if (available) {
      submit(values)
      invalidateQueries('formData')
    }
  }
  return (
    <CmschPage>
      <Helmet title={name} />
      <Box w="30rem" maxW="100%" mx="auto">
        <Heading>{name}</Heading>
        <FormStatusBadge status={status} />
        {}
        <form onSubmit={handleSubmit(onSubmit)}>
          {formFields.map((formField) => (
            <FormControl key={formField.fieldName} mt={5}>
              {formField.label && (
                <FormLabel mb={2} fontSize={20} htmlFor={formField.fieldName}>
                  {formField.label}
                </FormLabel>
              )}
              <AutoFormField
                disabled={(status !== FormStatus.NO_SUBMISSION && formField.permanent) || !available}
                control={control}
                fieldProps={formField}
              />
              {formField.note && <Markdown text={formField.note} />}
            </FormControl>
          ))}
          <Button disabled={!available} type="submit" isLoading={submitLoading}>
            Beküldés
          </Button>
        </form>
      </Box>
    </CmschPage>
  )
}

export default FormPage
