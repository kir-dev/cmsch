import React from 'react'
import { useParams } from 'react-router-dom'
import { Page } from 'components/@layout/Page'
import { Alert, AlertIcon, Box, Button, Flex, FormControl, FormLabel, Heading, Image, Input, useColorModeValue } from '@chakra-ui/react'
import axios from 'axios'
import { Riddle } from 'types/dto/riddles'
import { API_BASE_URL } from 'utils/configurations'

type RiddleProps = {}
export const RiddlePage: React.FC<RiddleProps> = (props) => {
  const { id } = useParams()
  if (!id) {
    return null
  }
  const submitUrl = `/api/riddle/${id}`
  const bg = useColorModeValue('brand.200', 'brand.600')
  const hover = useColorModeValue('brand.300', 'brand.700')
  const active = useColorModeValue('brand.500', 'brand.500')

  const [riddle, setRiddle] = React.useState<Riddle>({ id: 1, title: '', imageUrl: '', solved: false, hint: undefined })

  React.useEffect(() => {
    axios.get<Riddle>(`${API_BASE_URL}/api/riddle/${id}`).then((res) => {
      console.log(res)
      setRiddle(res.data)
    })
  }, [setRiddle])

  return (
    <Page {...props}>
      <Flex align="center">
        <Heading> {riddle.title} </Heading>
      </Flex>

      <Box>
        <Image width="100%" src={riddle.imageUrl} alt="Riddle Kép" borderRadius="md" />
        {riddle.hint && (
          <Alert status="info" mt={2} borderRadius="md">
            <AlertIcon />
            {riddle.hint}
          </Alert>
        )}
      </Box>

      <Box as="form" action={submitUrl} borderWidth={2} borderColor="brand.200" borderRadius="md" mt={2} p={5}>
        <FormControl>
          <FormLabel htmlFor="solution">Megoldásom:</FormLabel>
          <Input id="solution" name="solution" autoComplete="off" readOnly={riddle.solved} />
        </FormControl>

        <Button type="submit" width="100%" bg={bg} mt={10} _hover={{ background: hover }} _active={{ bg: active }}>
          Beadom
        </Button>
      </Box>
    </Page>
  )
}
