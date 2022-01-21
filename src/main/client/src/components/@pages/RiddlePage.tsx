import React, { FormEvent, useRef } from 'react'
import { useParams } from 'react-router-dom'
import { Page } from 'components/@layout/Page'
import { Alert, AlertIcon, Box, Button, Flex, FormControl, FormLabel, Heading, Image, Input, useColorModeValue } from '@chakra-ui/react'
import axios from 'axios'
import { Hint, Riddle, RiddleSubmissonResult, RiddleSubmissonStatus } from 'types/dto/riddles'
import { API_BASE_URL } from 'utils/configurations'

type RiddleProps = {}

export const RiddlePage: React.FC<RiddleProps> = (props) => {
  const { id } = useParams()
  if (!id) {
    return null
  }
  const bg = useColorModeValue('brand.200', 'brand.600')
  const hover = useColorModeValue('brand.300', 'brand.700')
  const active = useColorModeValue('brand.500', 'brand.500')

  const [riddle, setRiddle] = React.useState<Riddle>({ id: 1, title: '', imageUrl: '', solved: false, hint: undefined })

  const solutionInput = useRef<HTMLInputElement>(null)

  React.useEffect(() => {
    axios.get<Riddle>(`${API_BASE_URL}/api/riddle/${id}`).then((res) => {
      setRiddle(res.data)
    })
  }, [setRiddle])

  function submitSolution(event: FormEvent) {
    event.preventDefault()
    console.log(event)
    const solution = solutionInput?.current?.value
    axios.post<RiddleSubmissonResult>(`${API_BASE_URL}/api/riddle/${id}`, { solution: solution }).then((res) => {
      console.log(res)
      if (res.data.status == RiddleSubmissonStatus.WRONG) {
        console.log('wrong')
      }
      if (res.data.status == RiddleSubmissonStatus.CORRECT && res.data.nextId) {
        console.log('open next riddle')
      }
      if (res.data.status == RiddleSubmissonStatus.CORRECT && !res.data.nextId) {
        console.log('mind kesz')
      }
    })
  }

  function getHint() {
    return axios.get<Hint>(`${API_BASE_URL}/api/riddle/${id}/hint`).then((res) => {
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
        <Image width="100%" src={riddle.imageUrl} alt="Riddle Kép" borderRadius="md" />
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
