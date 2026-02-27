import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useRiddleDetailsQuery } from '@/api/hooks/riddle/useRiddleDeatilsQuery'
import { useRiddleHintQuery } from '@/api/hooks/riddle/useRiddleHintQuery'
import { useRiddleSkipMutation } from '@/api/hooks/riddle/useRiddleSkipMutation'
import { useRiddleSubmitMutation } from '@/api/hooks/riddle/useRiddleSubmitMutation'
import { ConfirmDialogButton } from '@/common-components/ConfirmDialogButton'
import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { StopItModal } from '@/common-components/StopItModal'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from '@/hooks/use-toast'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import { RiddleSubmissionStatus } from '@/util/views/riddle.view'
import { Info } from 'lucide-react'
import { type FormEvent, useRef, useState } from 'react'
import { Navigate, useNavigate, useParams } from 'react-router'

const RiddlePage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [isStopItOpen, setIsStopItOpen] = useState(false)
  const { toast } = useToast()
  const solutionInput = useRef<HTMLInputElement>(null)
  const { isError, isLoading, data } = useRiddleDetailsQuery(id || '')
  const hintQuery = useRiddleHintQuery(id || '')
  const submissionMutation = useRiddleSubmitMutation(() => setIsStopItOpen(true))
  const skipMutation = useRiddleSkipMutation()
  const [allowSubmission, setAllowSubmission] = useState(true)
  const riddleConfig = useConfigContext()?.components?.riddle

  if (!id) return <Navigate to={AbsolutePaths.RIDDLE} />

  if (isError || isLoading || !data || !riddleConfig) return <PageStatus isLoading={isLoading} isError={isError || !riddleConfig} />

  const submitSolution = (event: FormEvent) => {
    if (!allowSubmission) return
    setAllowSubmission(false)
    setTimeout(() => setAllowSubmission(true), 1000)
    solutionInput.current?.blur()
    event.preventDefault()
    const solution = solutionInput?.current?.value
    submissionMutation.mutate(
      { solution: solution || '', id: id || '' },
      {
        onSuccess: (result) => {
          if (result.status === RiddleSubmissionStatus.WRONG) {
            toast({
              title: l('riddle-incorrect-title'),
              description: l('riddle-incorrect-description'),
              variant: 'destructive'
            })
            solutionInput.current?.focus()
          }
          if (result.status === RiddleSubmissionStatus.SUBMITTER_BANNED) {
            toast({
              title: l('riddle-submitter-banned-title'),
              description: l('riddle-submitter-banned-description'),
              variant: 'destructive'
            })
          }
          if (result.status === RiddleSubmissionStatus.CORRECT && result.nextRiddles.length) {
            navigate(`${AbsolutePaths.RIDDLE}/solve/${result.nextRiddles[0].id}`)
            if (solutionInput.current) solutionInput.current.value = ''
            toast({
              title: l('riddle-correct-title'),
              description: l('riddle-correct-description')
            })
          }
          if (result.status === RiddleSubmissionStatus.CORRECT && !result.nextRiddles.length) {
            navigate(AbsolutePaths.RIDDLE)
            toast({
              title: l('riddle-completed-title'),
              description: l('riddle-completed-description')
            })
          }
        },
        onError: () => {
          toast({ title: l('riddle-submission-failed'), variant: 'destructive' })
        }
      }
    )
  }

  const skipSolution = () => {
    if (riddleConfig.skipEnabled && data.skipPermitted) {
      skipMutation.mutate(id, {
        onSuccess: (result) => {
          if (result.nextRiddles.length) {
            navigate(`${AbsolutePaths.RIDDLE}/solve/${result.nextRiddles[0].id}`)
            if (solutionInput.current) solutionInput.current.value = ''
            toast({
              title: l('riddle-skipped-title'),
              description: l('riddle-skipped-description')
            })
          }
          if (result.status === RiddleSubmissionStatus.CORRECT && !result.nextRiddles.length) {
            navigate(AbsolutePaths.RIDDLE)
            toast({
              title: l('riddle-completed-title'),
              description: l('riddle-completed-description')
            })
          }
        },
        onError: () => {
          toast({ title: l('riddle-skipping-failed'), variant: 'destructive' })
        }
      })
    }
  }

  const breadcrumbItems = [
    {
      title: 'Riddle',
      to: AbsolutePaths.RIDDLE
    },
    {
      title: data.title
    }
  ]

  return (
    <CmschPage loginRequired={true} title={data.title}>
      <CustomBreadcrumb items={breadcrumbItems} />
      <StopItModal isOpen={isStopItOpen} onClose={() => setIsStopItOpen(false)} />
      <h1 className="my-5 text-4xl font-bold tracking-tight"> {data.title} </h1>
      <div className="mx-auto w-full max-w-[30rem]">
        {data.imageUrl && <img className="w-full rounded-md" src={data.imageUrl} alt="Riddle Kép" />}
        <div className="mt-5 flex flex-col items-start gap-1">
          {data.creator && <p>Létrehozó: {data.creator}</p>}
          {data.firstSolver && <p>Első megoldó: {data.firstSolver}</p>}
          {data.description && <Markdown text={data.description} />}
        </div>
        <form onSubmit={submitSolution} className="mt-5 rounded-md border p-5 bg-card text-card-foreground">
          <div className="flex flex-col gap-2">
            <Label htmlFor="solution">Megoldásom:</Label>
            <Input ref={solutionInput} id="solution" name="solution" autoComplete="off" readOnly={data.solved} />
          </div>

          <div className="mt-10 flex flex-col gap-5">
            <Button disabled={!allowSubmission} type="submit" className="w-full">
              {!allowSubmission ? 'Küldés...' : 'Beadom'}
            </Button>
            {hintQuery.isSuccess || data.hint ? (
              <Alert>
                <Info className="h-4 w-4" />
                <AlertTitle>Hint</AlertTitle>
                <AlertDescription>{hintQuery.data?.hint || data.hint}</AlertDescription>
              </Alert>
            ) : (
              <ConfirmDialogButton
                buttonVariant="outline"
                buttonText="Hintet kérek"
                headerText="Hint kérés"
                bodyText="Biztos hintet szeretnél kérni?"
                confirmButtonText="Hint kérése"
                confirmAction={async () => {
                  await hintQuery.refetch()
                }}
              />
            )}
            {riddleConfig.skipEnabled && (
              <>
                <Alert>
                  <Info className="h-4 w-4" />
                  <AlertDescription>
                    Kihagyhatjátok a riddlet, ha már {riddleConfig.skipAfterGroupsSolved} csapat megoldotta. Ilyenkor 0 pontot kaptok érte.
                  </AlertDescription>
                </Alert>
                {data.skipPermitted ? (
                  <ConfirmDialogButton
                    buttonVariant="outline"
                    buttonText="Riddle kihagyása"
                    headerText="Riddle kihagyása"
                    bodyText="Biztosan kihagyod ezt a riddlet? Így nem kaptok pontot érte."
                    confirmButtonText="Riddle kihagyása"
                    confirmAction={skipSolution}
                  />
                ) : (
                  <Button className="w-full" variant="outline" disabled>
                    Riddle kihagyása
                  </Button>
                )}
              </>
            )}
          </div>
        </form>
      </div>
    </CmschPage>
  )
}

export default RiddlePage
