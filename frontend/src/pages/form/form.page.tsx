import { FunctionComponent, useEffect } from 'react'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { Box, Button, FormControl, FormLabel, Heading, useToast } from '@chakra-ui/react'
import { Navigate, useParams } from 'react-router-dom'
import { Helmet } from 'react-helmet-async'
import { AbsolutePaths } from '../../util/paths'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useFormPage } from '../../api/hooks/form/useFormPage'
import { AutoFormField } from './components/autoFormField'
import { useForm } from 'react-hook-form'
import Markdown from '../../common-components/Markdown'
import { useFormSubmit } from '../../api/hooks/form/useFormSubmit'
import { FormStatusBadge } from './components/formStatusBadge'
import { FormStatus, FormSubmitMessage, FormSubmitResult } from '../../util/views/form.view'
import Cookies from 'js-cookie'
import { CookieKeys } from '../../util/configs/cookies.config'
import { useTokenRefresh } from '../../api/hooks/token/useTokenRefresh'
import { l } from '../../util/language'
import { LoadingPage } from '../loading/loading.page'

interface FormPageProps {}

const FormPage: FunctionComponent<FormPageProps> = () => {
  const params = useParams()
  const { submit, submitLoading, result } = useFormSubmit(params.slug || '')
  const { data, isLoading, error, refetch } = useFormPage(params.slug || '')
  const { refresh } = useTokenRefresh()
  const { sendMessage } = useServiceContext()
  const toast = useToast()
  const { control, handleSubmit } = useForm()

  useEffect(() => {
    if (result) {
      const success = result === FormSubmitResult.OK || result === FormSubmitResult.OK_RELOG_REQUIRED
      toast({ title: FormSubmitMessage[result as FormSubmitResult], status: success ? 'success' : 'error' })
    }
    if (result === FormSubmitResult.OK_RELOG_REQUIRED && Cookies.get(CookieKeys.JWT_TOKEN)) {
      refresh((token) => Cookies.set(CookieKeys.JWT_TOKEN, token))
    }
    refetch()
  }, [result])

  if (isLoading) {
    return <LoadingPage />
  }

  if (error) {
    sendMessage(l('form-load-failed'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof data === 'undefined') {
    sendMessage(l('form-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
  const { form, submission, message, status, detailsValidated } = data
  const available = form && form.availableFrom * 1000 < Date.now() && form.availableUntil * 1000 > Date.now() && !detailsValidated
  const onSubmit = (values: Object) => {
    if (available) {
      submit(values, status !== FormStatus.NO_SUBMISSION)
      window.scrollTo(0, 0)
    }
  }
  if (status === FormStatus.NOT_FOUND || status === FormStatus.NOT_ENABLED || status === FormStatus.GROUP_NOT_PERMITTED)
    return <Navigate to="/" replace />
  return (
    <CmschPage>
      <Helmet title={form?.name || 'Űrlap'} />
      <Box w="100%" mx="auto">
        <Heading>{form?.name || 'Űrlap'}</Heading>
        <FormStatusBadge status={status} />

        {(submission?.rejectionMessage || message) && (
          <>
            <Markdown text={submission?.rejectionMessage || message} />
          </>
        )}
        {form && (
          <form onSubmit={handleSubmit(onSubmit)}>
            {form.formFields.map((formField) => (
              <FormControl key={formField.fieldName} mt={5}>
                {formField.label && (
                  <FormLabel mb={2} fontSize={20} htmlFor={formField.fieldName}>
                    {formField.label}
                  </FormLabel>
                )}
                <AutoFormField
                  defaultValue={submission?.[formField.fieldName]}
                  disabled={(status !== FormStatus.NO_SUBMISSION && formField.permanent) || !available}
                  control={control}
                  fieldProps={formField}
                />
                {formField.note && <Markdown text={formField.note} />}
              </FormControl>
            ))}
            <Button mt={5} disabled={!available} type="submit" isLoading={submitLoading}>
              {status === FormStatus.NO_SUBMISSION ? 'Beküldés' : 'Mentés'}
            </Button>
          </form>
        )}
      </Box>
    </CmschPage>
  )
}

export default FormPage
