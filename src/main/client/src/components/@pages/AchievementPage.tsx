import { Box, Button, Flex, FormLabel, Heading, Skeleton, Spacer, Stack, Text, Textarea, Image } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { useParams } from 'react-router-dom'

import { mockData } from './AchievementList'
import FilePicker from '../@commons/FilePicker'
import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'
import { Paragraph } from 'components/@commons/Basics'

type AchievementPageProps = {}

export const AchievementPage: React.FC<AchievementPageProps> = (props) => {
  const [textAnswer, setTextAnswer] = React.useState('')
  const { id } = useParams()
  if (!id) {
    return null
  }
  // ez csúnya tudom de itt úgyis api hívás lesz
  const data = mockData.achievements.filter((ach) => ach.achievement.id === parseInt(id))[0]

  const handleFileChange = (fileList: Array<File>) => {
    console.log(fileList)
  }

  const textAllowed = data.achievement.type === 'TEXT' || data.achievement.type === 'BOTH'
  const imageAllowed = data.achievement.type === 'IMAGE' || data.achievement.type === 'BOTH'

  const textInput = textAllowed ? (
    <Box>
      <FormLabel htmlFor="textAnswer">Szöveges válasz</FormLabel>
      <Textarea
        id="textAnswer"
        name="textAnswer"
        value={textAnswer}
        onChange={(e) => setTextAnswer(e.target.value)}
        placeholder="Szöveges válasz"
      />
    </Box>
  ) : null

  const fileInput = imageAllowed ? (
    <Box>
      <FormLabel>Csatolt fájl</FormLabel>
      <FilePicker onFileChange={handleFileChange} placeholder="Csatolt fájl" clearButtonLabel="Törlés" accept=".png,.jpeg,.jpg,.gif" />
    </Box>
  ) : null

  return (
    <Page {...props}>
      <Stack>
        <Flex align="center">
          <Heading>{data.achievement.title}</Heading>
          <Spacer />
          <AchievementStatusBadge status={data.status} fontSize="lg" />
        </Flex>
        <Stack>
          <Skeleton height="20px" />
          <Skeleton height="20px" />
          <Skeleton height="20px" />
        </Stack>
        {data.status === 'NOT_SUBMITTED' ? (
          <form>
            <Stack>
              {textInput}
              {fileInput}
              <Box>
                <Button mt={4} colorScheme="brand" type="submit">
                  Küldés
                </Button>
              </Box>
            </Stack>
          </form>
        ) : (
          <>
            <Heading size="lg">Beküldött megoldás</Heading>
            {textAllowed && data.submission ? <Paragraph>{data.submission.textAnswer}</Paragraph> : null}
            {imageAllowed && data.submission ? (
              <Box>
                <Image src={data.submission.imageUrlAnswer} alt="Beküldött megoldás" />
              </Box>
            ) : null}
            <Heading size="lg">Értékelés</Heading>
            <Flex>
              <Text>Státusz:&nbsp;</Text>
              <AchievementStatusBadge status={data.status} fontSize="lg" />
            </Flex>

            {(data.status === 'ACCEPTED' || data.status === 'REJECTED') && data.submission ? (
              <>
                <Text>Javító üzenete: {data.comment}</Text>
                <Text>Pont: {data.submission.score} pont</Text>
              </>
            ) : null}
          </>
        )}
      </Stack>
    </Page>
  )
}
