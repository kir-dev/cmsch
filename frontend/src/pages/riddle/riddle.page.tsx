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
import { FormEvent, useEffect, useRef, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { Hint, Riddle, RiddleSubmissonResult, RiddleSubmissonStatus } from '../../util/views/riddle.view'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { AbsolutePaths, Paths } from '../../util/paths'

const RiddlePage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()
  const toastIdRef = useRef<ToastId | null>(null)

  const [riddle, setRiddle] = useState<Riddle>({ id: 1, title: '', imageUrl: '', solved: false, hint: undefined })

  const [loading, setLoading] = useState<boolean>(false)
  const solutionInput = useRef<HTMLInputElement>(null)

  useEffect(() => {
    setLoading(true)
    axios
      .get<Riddle>(`/api/${Paths.RIDDLE}/${id}`)
      .then((res) => {
        setRiddle(res.data)
        setLoading(false)
      })
      .catch(() => {
        console.error('Nem sikerült lekérni a Riddle-t.')
      })
  }, [setRiddle])

  if (!id) return <Navigate to="/" replace />

  const submitSolution = (event: FormEvent) => {
    event.preventDefault()
    const solution = solutionInput?.current?.value
    axios
      .post<RiddleSubmissonResult>(`/api/${Paths.RIDDLE}/${id}`, { solution: solution })
      .then((res) => {
        if (res.data.status === RiddleSubmissonStatus.WRONG) {
          if (toastIdRef.current) {
            toast.close(toastIdRef.current)
          }
          toastIdRef.current =
            toast({
              title: 'Helytelen válasz!',
              description: 'Próbálja meg újra, sikerülni fog!',
              status: 'error',
              duration: 9000,
              isClosable: true
            }) || null
        }
        if (res.data.status === RiddleSubmissonStatus.CORRECT && res.data.nextId) {
          navigate(`${AbsolutePaths.RIDDLE}/${res.data.nextId}`)
          const input = document.getElementById('solution') as HTMLInputElement
          input.value = ''
          axios.get<Riddle>(`/api/${Paths.RIDDLE}/${res.data.nextId}`).then((resp) => {
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
          navigate(AbsolutePaths.RIDDLE)
          toast({
            title: 'Minden megvan!',
            description: 'Igazán szép munka, kolléga!',
            status: 'success',
            duration: 9000,
            isClosable: true
          })
        }
      })
      .catch(() => {
        console.error('Nem sikerült beküldeni a Riddle-t.', { toast: true })
      })
  }

  const getHint = () => {
    return axios
      .put<Hint>(`/api/${Paths.RIDDLE}/${id}/hint`)
      .then((res) => {
        const newRiddle = { ...riddle, hint: res.data.hint }
        setRiddle(newRiddle)
      })
      .catch(() => {
        console.error('Nem sikerült lekérni a hint-et.', { toast: true })
      })
  }

  const breadcrumbItems = [
    {
      title: 'Riddle',
      to: AbsolutePaths.RIDDLE
    },
    {
      title: riddle.title
    }
  ]

  if (loading) return <Loading />

  return (
    <CmschPage loginRequired groupRequired>
      <Helmet title={riddle.title} />
      <CustomBreadcrumb items={breadcrumbItems} />

      <Flex align="center">
        <Heading> {riddle.title} </Heading>
      </Flex>
      <Box maxW="100%" w="30rem" mx="auto">
        {riddle.imageUrl && <Image width="100%" src={`/cdn/${riddle.imageUrl}`} alt="Riddle Kép" borderRadius="md" />}
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
    </CmschPage>
  )
}

export default RiddlePage
