import React, { FormEvent, useEffect, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Page } from 'components/@layout/Page'
import {
  Alert,
  AlertIcon,
  Box,
  Button,
  Flex,
  FormControl,
  FormLabel,
  Heading,
  Image,
  Input,
  ToastId,
  useColorModeValue,
  useToast,
  VStack
} from '@chakra-ui/react'
import axios from 'axios'
import { Hint, Riddle, RiddleSubmissonResult, RiddleSubmissonStatus } from 'types/dto/riddles'
import { API_BASE_URL } from 'utils/configurations'
import { Loading } from '../../utils/Loading'
import { useServiceContext } from '../../utils/useServiceContext'
import { Helmet } from 'react-helmet'
import { CustomBreadcrumb } from 'components/@commons/CustomBreadcrumb'

type RiddleProps = {}

export const RiddlePage: React.FC<RiddleProps> = (props) => {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const toastIdRef = React.useRef<ToastId | null>(null)

  const { throwError } = useServiceContext()

  if (!id) {
    navigate('/')
  }

  const [riddle, setRiddle] = React.useState<Riddle>({ id: 1, title: '', imageUrl: '', solved: false, hint: undefined })
  const [loading, setLoading] = React.useState<boolean>(false)

  const solutionInput = useRef<HTMLInputElement>(null)

  useEffect(() => {
    setLoading(true)
    axios
      .get<Riddle>(`${API_BASE_URL}/api/riddle/${id}`)
      .then((res) => {
        setRiddle(res.data)
        setLoading(false)
      })
      .catch(() => {
        throwError('Nem sikerült lekérni a Riddle-t.')
      })
  }, [setRiddle])

  function submitSolution(event: FormEvent) {
    event.preventDefault()
    const solution = solutionInput?.current?.value
    axios
      .post<RiddleSubmissonResult>(`${API_BASE_URL}/api/riddle/${id}`, { solution: solution })
      .then((res) => {
        if (res.data.status === RiddleSubmissonStatus.WRONG) {
          if (toastIdRef.current) {
            toast.close(toastIdRef.current)
          }
          toastIdRef.current =
            toast({
              title: 'Helytelen válasz!',
              description: 'Próbáld meg újra, sikerülni fog!',
              status: 'error',
              duration: 9000,
              isClosable: true
            }) || null
        }
        if (res.data.status === RiddleSubmissonStatus.CORRECT && res.data.nextId) {
          navigate(`/riddleok/${res.data.nextId}`)
          const input = document.getElementById('solution') as HTMLInputElement
          input.value = ''
          axios.get<Riddle>(`${API_BASE_URL}/api/riddle/${res.data.nextId}`).then((resp) => {
            setRiddle(resp.data)
          })
          toast({
            title: 'Helyes megoldás!',
            description: 'Csak így tovább!',
            status: 'success',
            duration: 9000,
            isClosable: true
          })
        }
        if (res.data.status === RiddleSubmissonStatus.CORRECT && !res.data.nextId) {
          navigate(`/riddleok/`)
          toast({
            title: 'Minden megvan!',
            description: 'Igazán ügyi voltál!',
            status: 'success',
            duration: 9000,
            isClosable: true
          })
        }
      })
      .catch(() => {
        throwError('Nem sikerült beküldeni a Riddle-t.', { toast: true })
      })
  }

  function getHint() {
    return axios
      .put<Hint>(`${API_BASE_URL}/api/riddle/${id}/hint`)
      .then((res) => {
        const newRiddle = { ...riddle, hint: res.data.hint }
        setRiddle(newRiddle)
      })
      .catch(() => {
        throwError('Nem sikerült lekérni a hint-et.', { toast: true })
      })
  }

  const breadcrumbItems = [
    {
      title: 'Riddle',
      to: '/riddleok'
    },
    {
      title: riddle.title
    }
  ]

  if (loading) return <Loading />

  return (
    <Page {...props} loginRequired groupRequired>
      <Helmet title={riddle.title} />
      <CustomBreadcrumb items={breadcrumbItems} />

      <Flex align="center">
        <Heading> {riddle.title} </Heading>
      </Flex>
      <Box maxW="100%" w="30rem" mx="auto">
        {riddle.imageUrl && <Image width="100%" src={`${riddle.imageUrl}`} alt="Riddle Kép" borderRadius="md" />}
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
            <Input ref={solutionInput} id="solution" name="solution" autoComplete="off" readOnly={riddle.solved} />
          </FormControl>

          <VStack spacing={5} mt={10}>
            <Button type="submit" colorScheme="brand" width="100%">
              Beadom
            </Button>
            {riddle.hint ? (
              <Alert status="info" borderRadius="md">
                <AlertIcon />
                {riddle.hint}
              </Alert>
            ) : (
              <Button onClick={getHint} width="100%" colorScheme="blue" variant="outline">
                Hintet kérek!
              </Button>
            )}
          </VStack>
        </Box>
      </Box>
    </Page>
  )
}
