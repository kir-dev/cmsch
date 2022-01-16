import { Badge, Box, Button, Flex, FormLabel, Heading, Skeleton, Spacer, Stack, Text, Textarea } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { useParams } from 'react-router-dom'

import { mockData, statusColorMap, statusTextMap } from './AchievementList'
import FilePicker from '../@commons/FilePicker'

type AchievementPageProps = {}

export const AchievementPage: React.FC<AchievementPageProps> = (props) => {
  const [textAnswer, setTextAnswer] = React.useState('')
  const { id }  = useParams()
  if (!id) {
    return null
  }
  const data = mockData.achievements.filter(ach => ach.achievement.id === parseInt(id))[0]

  const handleFileChange = (fileList: Array<File>) => {
    fileList.forEach(file => console.log(file))
  }

  const textInput = data.achievement.type !== 'TEXT' && data.achievement.type !== 'BOTH' ? null : (
    <Box>
      <FormLabel htmlFor="textAnswer">Szöveges válasz</FormLabel>
      <Textarea id="textAnswer" name="textAnswer" value={textAnswer} onChange={(e) => setTextAnswer(e.target.value)} placeholder="Szöveges válasz" />
    </Box>
  )

  const fileInput = data.achievement.type !== 'IMAGE' && data.achievement.type !== 'BOTH' ? null : (
    <Box>
      <FormLabel>Csatolt fájl</FormLabel>
      <FilePicker onFileChange={handleFileChange} placeholder="Csatolt fájl" clearButtonLabel='Törlés' accept=".png,.jpeg,.jpg,.gif" />
    </Box>
  )

  const form = data.status !== 'NOT_SUBMITTED' ? null : (
    <form>
      <Stack>
        {textInput}
        {fileInput}
        <Box>
          <Button
            mt={4}
            backgroundColor="brand.400"
            _hover={{ bg: "brand.500" }}
            _active={{ bg: "brand.500" }}
            color="white"
            type="submit"
          >
            Küldés
          </Button>
        </Box>
      
        </Stack>
    </form>
  )

  return (
    <Page {...props}>
      <Stack>
        <Flex align='center'>
          <Heading>{data.achievement.title}</Heading>
          <Spacer />
          <Box>
            <Badge colorScheme={statusColorMap.get(data.status)}><Text fontSize='lg'>{statusTextMap.get(data.status)}</Text></Badge>
          </Box>
        </Flex>
        <Stack>
          <Skeleton height='20px' />
          <Skeleton height='20px' />
          <Skeleton height='20px' />
        </Stack>
        
        {form}
      </Stack>
      
    </Page>
  )
}
