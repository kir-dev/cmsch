import {
  Alert,
  AlertIcon,
  Box,
  Button,
  FormControl,
  FormLabel,
  Heading,
  Image,
  Input,
  Text,
  ToastId,
  useColorModeValue,
  useToast,
  VStack
} from '@chakra-ui/react'
import { FormEvent, useRef, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { RiddleSubmissonStatus } from '../../util/views/riddle.view'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { AbsolutePaths } from '../../util/paths'
import { l } from '../../util/language'
import { useRiddleDetailsQuery } from '../../api/hooks/riddle/useRiddleDeatilsQuery'
import { useRiddleSubmitMutation } from '../../api/hooks/riddle/useRiddleSubmitMutation'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { useRiddleHintQuery } from '../../api/hooks/riddle/useRiddleHintQuery'
import { ConfirmDialogButton } from '../../common-components/ConfirmDialogButton'
import { PageStatus } from '../../common-components/PageStatus'

const RiddlePage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const toastIdRef = useRef<ToastId | null>(null)
  const solutionInput = useRef<HTMLInputElement>(null)
  const { isError, isLoading, data } = useRiddleDetailsQuery(id || '')
  const hintQuery = useRiddleHintQuery(id || '')
  const submissionMutation = useRiddleSubmitMutation()
  const [allowSubmission, setAllowSubmission] = useState(true)

  if (!id) return <Navigate to={AbsolutePaths.RIDDLE} />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

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
          if (result.status === RiddleSubmissonStatus.WRONG) {
            if (toastIdRef.current) {
              toast.close(toastIdRef.current)
            }
            toastIdRef.current =
              toast({
                title: l('riddle-incorrect-title'),
                description: l('riddle-incorrect-description'),
                status: 'error',
                duration: 5000,
                isClosable: true
              }) || null
          }
          if (result.status === RiddleSubmissonStatus.CORRECT && result.nextId) {
            navigate(`${AbsolutePaths.RIDDLE}/${result.nextId}`)
            const input = document.getElementById('solution') as HTMLInputElement
            input.value = ''
            toast({
              title: l('riddle-correct-title'),
              description: l('riddle-correct-description'),
              status: 'success',
              duration: 5000,
              isClosable: true
            })
          }
          if (result.status === RiddleSubmissonStatus.CORRECT && !result.nextId) {
            navigate(AbsolutePaths.RIDDLE)
            toast({
              title: l('riddle-completed-title'),
              description: l('riddle-completed-description'),
              status: 'success',
              duration: 5000,
              isClosable: true
            })
          }
        },
        onError: () => {
          toast({ title: l('riddle-submission-failed'), status: 'error' })
        }
      }
    )
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
    <CmschPage loginRequired>
      <Helmet title={data.title} />
      <CustomBreadcrumb items={breadcrumbItems} />

      <Heading my={5}> {data.title} </Heading>
      <Box maxW="100%" w="30rem" mx="auto">
        {data.imageUrl && <Image width="100%" src={`${API_BASE_URL}/cdn/${data.imageUrl}`} alt="Riddle Kép" borderRadius="md" />}
        <VStack mt={5} align="flex-start">
          {data.creator && <Text>Létrehozó: {data.creator}</Text>}
          {data.firstSolver && <Text>Első megoldó: {data.firstSolver}</Text>}
        </VStack>
        <Box
          as="form"
          onSubmit={submitSolution}
          borderWidth={2}
          borderColor={useColorModeValue('gray.200', 'gray.600')}
          borderRadius="md"
          p={5}
          mt={5}
        >
          <FormControl>
            <FormLabel htmlFor="solution">Megoldásom:</FormLabel>
            <Input ref={solutionInput} id="solution" name="solution" autoComplete="off" readOnly={data.solved} />
          </FormControl>

          <VStack spacing={5} mt={10}>
            <Button isLoading={!allowSubmission} loadingText="Küldés..." type="submit" colorScheme="brand" width="100%">
              Beadom
            </Button>
            {hintQuery.isSuccess || data.hint ? (
              <Alert status="info" borderRadius="md">
                <AlertIcon />
                {hintQuery.data?.hint || data.hint}
              </Alert>
            ) : (
              <>
                <ConfirmDialogButton
                  buttonColorSchene="blue"
                  buttonVariant="outline"
                  buttonWidth="100%"
                  buttonText="Hintet kérek"
                  headerText="Hint kérés"
                  bodyText="Biztos hintet szeretnél kérni?"
                  confirmButtonText="Hint kérése"
                  confirmAction={() => hintQuery.refetch()}
                />
              </>
            )}
          </VStack>
        </Box>
      </Box>
    </CmschPage>
  )
}

export default RiddlePage
