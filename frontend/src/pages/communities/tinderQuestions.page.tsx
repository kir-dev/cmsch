import { Box, Button, FormControl, FormLabel, Heading, useToast, Wrap, WrapItem } from '@chakra-ui/react'
import { useEffect } from 'react'
import { FormProvider, useForm } from 'react-hook-form'
import { Link } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useTinderAnswers } from '../../api/hooks/community/useTinderAnswers.ts'
import { useTinderAnswerSend } from '../../api/hooks/community/useTinderAnswerSend.ts'
import { useTinderQuestions } from '../../api/hooks/community/useTinderQuestions.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { useBrandColor } from '../../util/core-functions.util.ts'
import { AbsolutePaths } from '../../util/paths.ts'
import { SendAnswerResponseMessage, SendAnswerResponseStatus } from '../../util/views/tinder.ts'

const TinderQuestionsPage = () => {
  const brandColor = useBrandColor()
  const toast = useToast()
  const formMethods = useForm<Record<string, string>>()
  const { setValue, watch } = formMethods

  const config = useConfigContext()?.components
  const component = config?.communities
  const app = config?.app

  const { data: questions, isLoading: questionsLoading, isError: questionsError } = useTinderQuestions()
  const { data: answersStatus, isLoading: answersLoading, isError: answersError, refetch: refetchAnswers } = useTinderAnswers()
  const { response, submit } = useTinderAnswerSend()

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
    toast({ title, status: success ? 'success' : 'error' })
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
  const isError = questionsError || answersError
  const data = questions && answersStatus
  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title="Tinder kérdések" />

  const onSubmit = (values: Record<string, string>) => {
    submit(values, answersStatus.answered)
  }

  return (
    <CmschPage loginRequired>
      <title>{app?.siteName || 'CMSch'} | Tinder kérdések</title>
      <Box w="100%" mx="auto">
        <Box
          position="relative"
          mb={6}
          display="flex"
          flexDirection={{ base: 'column', sm: 'row' }}
          alignItems={{ base: 'center', md: 'flex-start' }}
          gap={4}
        >
          <Heading as="h1" variant="main-title">
            Tinder kérdések
          </Heading>
          <Button
            as={Link}
            to={`${AbsolutePaths.TINDER}/community`}
            size={{ base: 'md', md: 'lg' }}
            aria-label="Tinder-matches-button"
            width={{ base: 'full', sm: 'auto' }}
            position={{ base: 'relative', sm: 'absolute' }}
            top={{ base: 'auto', sm: '50%' }}
            right={{ base: 'auto', sm: 2 }}
            transform={{ base: 'none', sm: 'translateY(-30%)', md: 'translateY(-50%)' }}
          >
            Tinder
          </Button>
        </Box>
        <FormProvider {...formMethods}>
          <form onSubmit={formMethods.handleSubmit(onSubmit)}>
            {questions.map((q) => {
              const fieldKey = String(q.id)
              const selected = watch(fieldKey) || ''

              return (
                <FormControl key={q.id} mt={5}>
                  <FormLabel mb={2} fontSize={20} htmlFor={fieldKey}>
                    {q.question}
                  </FormLabel>

                  <Wrap spacing={2} align="center">
                    {q.options.map((opt) => (
                      <WrapItem key={opt}>
                        <Button
                          type="button"
                          size="sm"
                          onClick={() => setValue(fieldKey, opt)}
                          variant={selected === opt ? 'solid' : 'outline'}
                          colorScheme={selected === opt ? brandColor : undefined}
                          aria-pressed={selected === opt}
                        >
                          {opt}
                        </Button>
                      </WrapItem>
                    ))}
                  </Wrap>
                </FormControl>
              )
            })}

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
