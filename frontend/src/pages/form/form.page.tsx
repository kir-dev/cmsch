import { Box, Button, Divider, FormControl, FormLabel, Heading, useToast } from '@chakra-ui/react'
import Cookies from 'js-cookie'
import { FunctionComponent, useEffect } from 'react'
import { Helmet } from 'react-helmet-async'
import { useForm } from 'react-hook-form'
import { Navigate, useParams } from 'react-router-dom'
import { useAuthContext } from '../../api/contexts/auth/useAuthContext'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useFormPage } from '../../api/hooks/form/useFormPage'
import { useFormSubmit } from '../../api/hooks/form/useFormSubmit'
import { useTokenRefresh } from '../../api/hooks/useTokenRefresh'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { CookieKeys } from '../../util/configs/cookies.config'
import { isCheckbox } from '../../util/core-functions.util'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { FormFieldVariants, FormStatus, FormSubmitMessage, FormSubmitResult } from '../../util/views/form.view'
import { AutoFormField } from './components/autoFormField'
import { FormStatusBadge } from './components/formStatusBadge'

interface FormPageProps {}

const FormPage: FunctionComponent<FormPageProps> = () => {
  const toast = useToast()
  const params = useParams()
  const { isLoggedIn } = useAuthContext()
  const { control, handleSubmit } = useForm()
  const { submit, submitLoading, result } = useFormSubmit(params.slug || '')
  const { data, isLoading, isError, refetch } = useFormPage(params.slug || '')
  const { refresh } = useTokenRefresh()
  const { sendMessage } = useServiceContext()

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

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Űrlap" />

  const { form, submission, message, status, detailsValidated } = data
  const available = form && form.availableFrom * 1000 < Date.now() && form.availableUntil * 1000 > Date.now() && !detailsValidated

  const onSubmit = (values: Object) => {
    if (available) {
      submit(values, status !== FormStatus.NO_SUBMISSION)
      window.scrollTo(0, 0)
    }
  }
  if (!isLoggedIn) return <ComponentUnavailable />
  if (status === FormStatus.NOT_FOUND || status === FormStatus.NOT_ENABLED || status === FormStatus.GROUP_NOT_PERMITTED) {
    if (status === FormStatus.NOT_FOUND) sendMessage(message ?? l('form-not-available'))
    else sendMessage(message ?? l('form-disabled'))

    return <Navigate to={AbsolutePaths.ERROR} />
  }
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
                {formField.type === FormFieldVariants.SECTION_START && <Divider mt={10} />}
                {formField.label && !isCheckbox(formField.type) && (
                  <FormLabel mb={2} fontSize={formField.type === FormFieldVariants.SECTION_START ? 30 : 20} htmlFor={formField.fieldName}>
                    {formField.label}
                  </FormLabel>
                )}
                <AutoFormField
                  submittedValue={submission?.[formField.fieldName]}
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
