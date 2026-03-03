import { useFormPage } from '@/api/hooks/form/useFormPage'
import { useFormSubmit } from '@/api/hooks/form/useFormSubmit'
import { useTokenRefresh } from '@/api/hooks/useTokenRefresh'
import { useEffect } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { useParams } from 'react-router'

import { useAuthContext } from '@/api/contexts/auth/useAuthContext.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { useToast } from '@/hooks/use-toast'
import { isCheckbox, isGridField } from '@/util/core-functions.util'
import { FormFieldVariants, FormStatus, FormSubmitMessage, FormSubmitResult } from '@/util/views/form.view'
import { AutoFormField } from './components/autoFormField'
import { FormStatusBadge } from './components/formStatusBadge'

const FormPage = () => {
  const { toast } = useToast()
  const params = useParams()
  const formMethods = useForm()
  const { submit, submitLoading, result } = useFormSubmit(params.slug || '')
  const { data, isLoading, isError, refetch } = useFormPage(params.slug || '')
  const tokenRefresh = useTokenRefresh()
  const { isLoggedIn } = useAuthContext()

  useEffect(() => {
    if (result) {
      const success = result === FormSubmitResult.OK || result === FormSubmitResult.OK_RELOG_REQUIRED
      toast({ title: FormSubmitMessage[result as FormSubmitResult], variant: success ? 'default' : 'destructive' })
    }
    if (result === FormSubmitResult.OK_RELOG_REQUIRED) {
      tokenRefresh.mutate()
    }
    refetch()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [result])

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Űrlap" />

  const { form, submission, message, status, detailsValidated } = data
  const available = form && form.availableFrom * 1000 < Date.now() && form.availableUntil * 1000 > Date.now() && !detailsValidated

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onSubmit = (values: Record<string, any>) => {
    const newValues: Record<string, string> = {}
    Object.keys(values).forEach((v) => {
      const formField = form?.formFields.find((ff) => ff.fieldName === v)
      if (isGridField(formField?.type)) {
        newValues[v] = JSON.stringify(values[v])
      } else {
        newValues[v] = values[v]
      }
    })
    if (available) {
      submit(newValues, status !== FormStatus.NO_SUBMISSION)
      window.scrollTo(0, 0)
    }
  }

  if (!isLoggedIn && status === FormStatus.NOT_FOUND) return <ComponentUnavailable />

  return (
    <CmschPage title={form?.name || 'Űrlap'}>
      <div className="w-full mx-auto">
        <h1 className="text-3xl font-bold font-heading">{form?.name || 'Űrlap'}</h1>
        <div className="mt-2">
          <FormStatusBadge status={status} />
        </div>

        {(submission?.rejectionMessage || message) && (
          <div className="mt-5">
            <Markdown text={submission?.rejectionMessage || message} />
          </div>
        )}

        {form && (
          <FormProvider {...formMethods}>
            <form onSubmit={formMethods.handleSubmit(onSubmit)} className="mt-5">
              {form.formFields.map((formField) => (
                <div key={formField.fieldName} className="mt-5">
                  {formField.type === FormFieldVariants.SECTION_START && <Separator className="mt-10 mb-5 h-px bg-border" />}
                  {formField.label && !isCheckbox(formField.type) && (
                    <Label
                      className={formField.type === FormFieldVariants.SECTION_START ? 'text-3xl' : 'text-xl'}
                      htmlFor={formField.fieldName}
                    >
                      {formField.label}
                    </Label>
                  )}
                  <div className="mt-2">
                    <AutoFormField
                      submittedValue={submission?.[formField.fieldName]}
                      disabled={(status !== FormStatus.NO_SUBMISSION && formField.permanent) || !available}
                      control={formMethods.control}
                      fieldProps={formField}
                    />
                  </div>
                  {formField.note && (
                    <div className="mt-1 text-sm text-muted-foreground">
                      <Markdown text={formField.note} />
                    </div>
                  )}
                </div>
              ))}
              <div className="flex justify-end mt-10">
                <Button className="bg-primary text-primary-foreground" disabled={!available || submitLoading} type="submit">
                  {status === FormStatus.NO_SUBMISSION ? 'Beküldés' : 'Mentés'}
                </Button>
              </div>
            </form>
          </FormProvider>
        )}
      </div>
    </CmschPage>
  )
}

export default FormPage
