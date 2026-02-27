import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useTinderAnswers } from '@/api/hooks/community/useTinderAnswers.ts'
import { useTinderAnswerSend } from '@/api/hooks/community/useTinderAnswerSend.ts'
import { useTinderInteractionReset } from '@/api/hooks/community/useTinderInteractionReset'
import { useTinderQuestions } from '@/api/hooks/community/useTinderQuestions.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { ConfirmDialogButton } from '@/common-components/ConfirmDialogButton'
import { CmschPage } from '@/common-components/layout/CmschPage.tsx'
import { PageStatus } from '@/common-components/PageStatus.tsx'
import { Button } from '@/components/ui/button'
import { useToast } from '@/hooks/use-toast'
import { useBrandColor } from '@/util/core-functions.util.ts'
import { AbsolutePaths } from '@/util/paths.ts'
import { SendAnswerResponseMessage, SendAnswerResponseStatus } from '@/util/views/tinder.ts'
import { useEffect } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { Link } from 'react-router'

const TinderQuestionsPage = () => {
  const brandColor = useBrandColor()
  const { toast } = useToast()
  const formMethods = useForm<Record<string, string>>()
  const { setValue, watch } = formMethods

  const component = useConfigContext()?.components?.communities

  const { data: questions, isLoading: questionsLoading, isError: questionsError } = useTinderQuestions()
  const { data: answersStatus, isLoading: answersLoading, isError: answersError, refetch: refetchAnswers } = useTinderAnswers()
  const { response, submit } = useTinderAnswerSend()

  const { mutateAsync: resetInteractions, isError: resetError } = useTinderInteractionReset()

  useEffect(() => {
    if (!questions || !answersStatus) return
    const defaults: Record<string, string> = {}
    const existing = answersStatus?.answer || {}
    for (const [k, v] of Object.entries(existing)) {
      defaults[String(k)] = String(v)
    }
    formMethods.reset(defaults)
  }, [questions, answersStatus, formMethods])

  useEffect(() => {
    if (!response) return

    const success = response === SendAnswerResponseStatus.OK
    const title = SendAnswerResponseMessage[response as SendAnswerResponseStatus]
    toast({ title, variant: success ? 'default' : 'destructive' })
    if (success && answersStatus?.answered) {
      //redirect to /tinder/community
      window.location.href = `${AbsolutePaths.TINDER}/community`
      return
    }
    refetchAnswers()
  }, [response, refetchAnswers, toast, answersStatus?.answered])

  // If the feature is disabled, show unavailable after hooks have been called
  if (!component || !component.tinderEnabled) return <ComponentUnavailable />

  const isLoading = questionsLoading || answersLoading
  const isError = questionsError || answersError || resetError
  const data = questions && answersStatus
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Tinder kérdések" />

  const onSubmit = (values: Record<string, string>) => {
    submit(values, answersStatus.answered)
  }

  const handleReset = async () => {
    try {
      await resetInteractions()
      toast({ title: 'Interakciók sikeresen törölve' })
      refetchAnswers()
    } catch (err) {
      console.error(err)
      toast({ title: 'Hiba történt az interakciók törlése során' })
    }
  }

  return (
    <CmschPage loginRequired={true} title="Tinder kérdések">
      <div className="w-full mx-auto">
        <div className="relative mb-6 flex flex-col sm:flex-row items-center md:items-start gap-4">
          <h1 className="text-3xl font-bold font-heading">Tinder kérdések</h1>
          <Button
            asChild
            size="lg"
            variant="outline"
            className="w-full sm:w-auto sm:absolute sm:top-1/2 sm:right-2 sm:-translate-y-1/2 md:-translate-y-1/2"
          >
            <Link to={`${AbsolutePaths.TINDER}/community`}>Tinder</Link>
          </Button>
          <ConfirmDialogButton
            headerText="Interakciók törlése"
            bodyText="Biztosan törölni szeretné az összes Tinder interakcióját? Ezt nem lehet visszacsinálni."
            buttonText="Interakciók törlése"
            buttonColorScheme="red"
            buttonVariant="outline"
            confirmButtonText="Törlés"
            refuseButtonText="Mégse"
            confirmAction={handleReset}
          />
        </div>
        <FormProvider {...formMethods}>
          <form onSubmit={formMethods.handleSubmit(onSubmit)}>
            {questions.map((q) => {
              const fieldKey = String(q.question)
              const selected = watch(fieldKey) || ''

              return (
                <div key={q.question} className="mt-5">
                  <label className="block mb-2 text-xl font-medium" htmlFor={fieldKey}>
                    {q.question}
                  </label>

                  <div className="flex flex-wrap gap-2 items-center">
                    {q.options.map((opt) => (
                      <div key={opt}>
                        <Button
                          type="button"
                          size="sm"
                          onClick={() => setValue(fieldKey, opt)}
                          variant={selected === opt ? 'default' : 'outline'}
                          style={selected === opt ? { backgroundColor: brandColor } : undefined}
                          aria-pressed={selected === opt}
                        >
                          {opt}
                        </Button>
                      </div>
                    ))}
                  </div>
                </div>
              )
            })}

            <Button style={{ backgroundColor: brandColor }} disabled={isLoading} type="submit" className="mt-6">
              Mentés
            </Button>
          </form>
        </FormProvider>
      </div>
    </CmschPage>
  )
}

export default TinderQuestionsPage
