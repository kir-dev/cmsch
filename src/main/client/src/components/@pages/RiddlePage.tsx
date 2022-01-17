import React from 'react'
import { useParams } from 'react-router-dom'
import { mockData } from './RiddleList'
import { Page } from 'components/@layout/Page'
import { Alert, AlertIcon, Box, Button, Flex, FormControl, FormLabel, Heading, Image, Input, useColorModeValue } from '@chakra-ui/react'

type RiddleProps = {}
export const RiddlePage: React.FC<RiddleProps> = (props) => {
  const { id } = useParams()
  if (!id) {
    return null
  }
  const submitUrl = `/api/riddle/${id}`
  const riddle = mockData.riddles.filter((ach) => ach.riddle.id === parseInt(id))[0].riddle
  console.log(riddle.id)
  const bg = useColorModeValue('brand.200', 'brand.600')
  const hover = useColorModeValue('brand.300', 'brand.700')
  const active = useColorModeValue('brand.500', 'brand.500')

  return (
    <Page {...props}>
      <Flex align="center">
        <Heading> {riddle.title} </Heading>
      </Flex>

      {riddle.solved && (
        <Alert status="success" mb={2} borderRadius="md" bgColor={bg}>
          <AlertIcon />
          Ezt a riddlet már sikeresen megoldottad!
        </Alert>
      )}

      <Box>
        <Image width="100%" src={riddle.imageUrl} alt="Riddle Kép" borderRadius="md" />
        <Alert status="info" mt={2} borderRadius="md">
          <AlertIcon />
          {riddle.hint}
        </Alert>
      </Box>

      <Box as="form" action={submitUrl} borderWidth={2} borderColor="brand.200" borderRadius="md" mt={2} p={2}>
        <FormControl>
          <FormLabel htmlFor="solution">Megoldás</FormLabel>
          <Input id="solution" name="solution" autoComplete="off" readOnly={riddle.solved} defaultValue={riddle.solution} />
        </FormControl>

        {!riddle.solved && (
          <Button type="submit" width="100%" bg={bg} mt={2} _hover={{ background: hover }} _active={{ bg: active }}>
            Beadom
          </Button>
        )}
      </Box>
    </Page>
  )
}
