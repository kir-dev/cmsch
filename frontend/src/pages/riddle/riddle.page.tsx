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
import { useRiddleDetailsQuery } from '../../api/hooks/useRiddleDeatilsQuery'
import { useServiceContext } from '../../api/contexts/service/ServiceContext'
import { useRiddleSubmitMutation } from '../../api/hooks/useRiddleSubmitMutation'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { useRiddleHintQuery } from '../../api/hooks/useRiddleHintQuery'
import { ConfirmDialogButton } from '../../common-components/ConfirmDialogButton'
import { LoadingPage } from '../loading/loading.page'

const RiddlePage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const toastIdRef = useRef<ToastId | null>(null)
  const { sendMessage } = useServiceContext()
  const solutionInput = useRef<HTMLInputElement>(null)
  const onRiddleQueryFailed = () => toast({ title: l('riddle-query-failed'), status: 'error' })
  const queryResult = useRiddleDetailsQuery(onRiddleQueryFailed, id || '')
  const hintQuery = useRiddleHintQuery(onRiddleQueryFailed, id || '')
  const submissionMutation = useRiddleSubmitMutation()
  const [allowSubmission, setAllowSubmission] = useState(true)

  if (queryResult.isError) {
    sendMessage(l('riddle-query-failed') + queryResult.error.message)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (queryResult.isLoading) {
    return <LoadingPage />
  }

  if (!id) return <Navigate to="/" replace />

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
      title: queryResult.data?.title
    }
  ]

  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title={queryResult.data?.title} />
      <CustomBreadcrumb items={breadcrumbItems} />

      <Heading my={5}> {queryResult.data?.title} </Heading>
      <Box maxW="100%" w="30rem" mx="auto">
        {queryResult.data?.imageUrl && (
          <Image width="100%" src={`${API_BASE_URL}/cdn/${queryResult.data?.imageUrl}`} alt="Riddle Kép" borderRadius="md" />
        )}
        <VStack mt={5} align="flex-start">
          {queryResult.data?.creator && <Text>Létrehozó: {queryResult.data?.creator}</Text>}
          {queryResult.data?.firstSolver && <Text>Első megoldó: {queryResult.data?.firstSolver}</Text>}
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
            <Input ref={solutionInput} id="solution" name="solution" autoComplete="off" readOnly={queryResult.data?.solved} />
          </FormControl>

          <VStack spacing={5} mt={10}>
            <Button isLoading={!allowSubmission} loadingText="Küldés..." type="submit" colorScheme="brand" width="100%">
              Beadom
            </Button>
            {hintQuery.isSuccess || queryResult.data?.hint ? (
              <Alert status="info" borderRadius="md">
                <AlertIcon />
                {hintQuery.data?.hint || queryResult.data?.hint}
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
