import { Box, Button, FormControl, FormLabel, Heading, useToast } from '@chakra-ui/react'
import { useEffect } from 'react'
import { FormProvider, type SubmitHandler, useForm } from 'react-hook-form'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useTinderAnswers } from '../../api/hooks/community/useTinderAnswers.ts'
import { useTinderAnswerSend } from '../../api/hooks/community/useTinderAnswerSend.ts'
import { useTinderQuestions } from '../../api/hooks/community/useTinderQuestions.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { type SendAnswerDto, SendAnswerResponseMessage, SendAnswerResponseStatus } from '../../util/views/tinder.ts'

const TinderQuestionsPage = () => {
  const brandColor = useBrandColor()
  const toast = useToast()
  const formMethods = useForm<Record<string, string>>()

  const config = useConfigContext()?.components
  const component = config?.communities
  const app = config?.app

  // Call hooks unconditionally to respect the rules of hooks
  const { data: questions, isLoading: questionsLoading, isError: questionsError } = useTinderQuestions()
  const { data: answersStatus, isLoading: answersLoading, isError: answersError, refetch: refetchAnswers } = useTinderAnswers()
  const { response, submit } = useTinderAnswerSend()

  // Ensure the effect is also registered unconditionally (before any early returns)
  useEffect(() => {
    if (!response) return

    const success = response === SendAnswerResponseStatus.OK
    const title = SendAnswerResponseMessage[response as SendAnswerResponseStatus]
    toast({ title, status: success ? 'success' : 'error' })
    refetchAnswers()
  }, [response, refetchAnswers, toast])

  // If the feature is disabled, show unavailable after hooks have been called
  if (!component || !component.tinderEnabled) return <ComponentUnavailable />

  const isLoading = questionsLoading || answersLoading
  const isError = questionsError || answersError
  const data = questions && answersStatus
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Tinder kérdések" />

  const onSubmit: SubmitHandler<Record<string, string>> = async (values) => {
    const map = new Map<number, string>()
    for (const [k, v] of Object.entries(values || {})) {
      const id = Number(k)
      if (!Number.isNaN(id) && v != null && v !== '') map.set(id, v)
    }
    submit({ answers: map }, (answersStatus as { answered: boolean; answer: SendAnswerDto })?.answered)
  }

  return (
    <CmschPage loginRequired>
      <title>{app?.siteName || 'CMSch'} | Tinder kérdések</title>
      <Box w="100%" mx="auto">
        <Heading as="h1" variant="main-title">
          Tinder kérdések
        </Heading>

        <FormProvider {...formMethods}>
          <form onSubmit={formMethods.handleSubmit(onSubmit)}>
            {questions.map((q) => (
              <FormControl key={q.id} mt={5}>
                <FormLabel mb={2} fontSize={20} htmlFor={String(q.id)}>
                  {q.question}
                </FormLabel>
              </FormControl>
            ))}

            <Button mt={6} colorScheme={brandColor} isLoading={isLoading} type="submit">
              Mentés
            </Button>
          </form>
        </FormProvider>
      </Box>
    </CmschPage>
  )
}

export default TinderQuestionsPage
