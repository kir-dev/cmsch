import React, { FormEvent, useEffect, useRef } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
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
  useColorModeValue,
  useToast
} from '@chakra-ui/react'
import axios from 'axios'
import { Hint, Riddle, RiddleSubmissonResult, RiddleSubmissonStatus } from 'types/dto/riddles'
import { API_BASE_URL } from 'utils/configurations'

type RiddleProps = {}

export const RiddlePage: React.FC<RiddleProps> = (props) => {
  const { id } = useParams()
  const navigate = useNavigate()
  const toast = useToast()

  if (!id) {
    return null
  }
  const bg = useColorModeValue('brand.200', 'brand.600')
  const hover = useColorModeValue('brand.300', 'brand.700')
  const active = useColorModeValue('brand.500', 'brand.500')

  const [riddle, setRiddle] = React.useState<Riddle>({ id: 1, title: '', imageUrl: '', solved: false, hint: undefined })

  const solutionInput = useRef<HTMLInputElement>(null)

  useEffect(() => {
    axios.get<Riddle>(`${API_BASE_URL}/api/riddle/${id}`).then((res) => {
      setRiddle(res.data)
    })
  }, [setRiddle])

  function submitSolution(event: FormEvent) {
    event.preventDefault()
    const solution = solutionInput?.current?.value
    axios.post<RiddleSubmissonResult>(`${API_BASE_URL}/api/riddle/${id}`, { solution: solution }).then((res) => {
      console.log(res)
      if (res.data.status == RiddleSubmissonStatus.WRONG) {
        toast({
          title: 'Helytelen válasz!',
          description: 'Próbáld meg újra, sikerülni fog!',
          status: 'error',
          duration: 9000,
          isClosable: true
        })
      }
      if (res.data.status == RiddleSubmissonStatus.CORRECT && res.data.nextId) {
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
      if (res.data.status == RiddleSubmissonStatus.CORRECT && !res.data.nextId) {
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
  }

  function getHint() {
    return axios.put<Hint>(`${API_BASE_URL}/api/riddle/${id}/hint`).then((res) => {
      const newRiddle = { ...riddle, hint: res.data.hint }
      setRiddle(newRiddle)
    })
  }

  return (
    <Page {...props}>
      <Flex align="center">
        <Heading> {riddle.title} </Heading>
      </Flex>

      <Box>
        <Image width="100%" src={`/cdn/${riddle.imageUrl}`} alt="Riddle Kép" borderRadius="md" />
        {riddle.hint && (
          <Alert status="info" my={5} borderRadius="md">
            <AlertIcon />
            {riddle.hint}
          </Alert>
        )}
        {!riddle.hint && (
          <Button onClick={getHint} width="100%" bg="blue.500" my={5}>
            Hintet kérek!
          </Button>
        )}
      </Box>

      <Box as="form" onSubmit={submitSolution} borderWidth={2} borderColor={bg} borderRadius="md" p={5}>
        <FormControl>
          <FormLabel htmlFor="solution">Megoldásom:</FormLabel>
          <Input ref={solutionInput} id="solution" name="solution" autoComplete="off" readOnly={riddle.solved} />
        </FormControl>

        <Button type="submit" width="100%" bg={bg} mt={10} _hover={{ background: hover }} _active={{ bg: active }}>
          Beadom
        </Button>
      </Box>
    </Page>
  )
}
