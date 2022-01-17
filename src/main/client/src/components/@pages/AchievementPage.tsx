import { Box, Button, Flex, FormLabel, Heading, Skeleton, Stack, Text, Textarea, Image } from '@chakra-ui/react'
import { Page } from '../@layout/Page'
import React from 'react'
import { useParams } from 'react-router-dom'

import { FilePicker } from '../@commons/FilePicker'
import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'
import { Paragraph } from 'components/@commons/Basics'
import { AchievementFullDetailsView, achievementType, achievementStatus } from '../../types/dto/achievements'

type AchievementPageProps = {}

const MOCK_DATA: AchievementFullDetailsView[] = [
  {
    achievement: {
      id: 1,
      categoryId: 1,
      title: `Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
        sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.`,
      description: 'blalblal',
      type: achievementType.BOTH
    },
    status: achievementStatus.ACCEPTED,
    //comment: 'Nice work!',
    submission: {
      id: 1,
      rejected: false,
      approved: true,
      imageUrlAnswer: 'https://via.placeholder.com/200',
      textAnswer: `Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
        Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
        Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`,
      score: 10
    }
  },
  {
    achievement: {
      id: 2,
      categoryId: 1,
      title: 'Lorem ipsum2',
      description: 'fdsfdsgsd',
      type: achievementType.TEXT
    },
    status: achievementStatus.REJECTED,
    //comment: 'nice try tho',
    submission: {
      id: 2,
      rejected: true,
      approved: false,
      textAnswer: `Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
        Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
        Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`,
      score: 0
    }
  },
  {
    achievement: {
      id: 3,
      categoryId: 1,
      title: 'Lorem ipsum3',
      description: 'fdsfdsgsd',
      type: achievementType.IMAGE
    },
    status: achievementStatus.SUBMITTED,
    submission: {
      id: 3,
      approved: false,
      rejected: false,
      imageUrlAnswer: 'https://via.placeholder.com/150',
      score: 0
    }
  },
  {
    achievement: {
      id: 4,
      categoryId: 1,
      title: 'Lorem ipsum4',
      description: 'fdsfdsgsd',
      type: achievementType.BOTH
    },
    status: achievementStatus.NOT_SUBMITTED
  }
]

export const AchievementPage: React.FC<AchievementPageProps> = (props) => {
  const [textAnswer, setTextAnswer] = React.useState('')
  const { id } = useParams()
  if (!id) {
    return null
  }
  // ez csúnya tudom de itt úgyis api hívás lesz
  const data = MOCK_DATA.filter((ach) => ach.achievement.id === parseInt(id))[0]

  const handleFileChange = (fileList: Array<File>) => {
    console.log(fileList)
  }

  const textAllowed = data.achievement.type === achievementType.TEXT || data.achievement.type === achievementType.BOTH
  const imageAllowed = data.achievement.type === achievementType.IMAGE || data.achievement.type === achievementType.BOTH

  const textInput = textAllowed && (
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
  )

  const fileInput = imageAllowed && (
    <Box>
      <FormLabel>Csatolt fájl</FormLabel>
      <FilePicker onFileChange={handleFileChange} placeholder="Csatolt fájl" clearButtonLabel="Törlés" accept=".png,.jpeg,.jpg,.gif" />
    </Box>
  )

  return (
    <Page {...props}>
      <Stack>
        <Heading marginBottom="0px">{data.achievement.title}</Heading>
        <AchievementStatusBadge status={data.status} fontSize="lg" />
        <Stack>
          <Skeleton height="20px" />
          <Skeleton height="20px" />
          <Skeleton height="20px" />
        </Stack>
        {data.status === achievementStatus.NOT_SUBMITTED ? (
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
            {textAllowed && data.submission && <Paragraph>{data.submission.textAnswer}</Paragraph>}
            {imageAllowed && data.submission && (
              <Box>
                <Image src={data.submission.imageUrlAnswer} alt="Beküldött megoldás" />
              </Box>
            )}
            <Heading size="lg">Értékelés</Heading>
            <Flex flexDirection="row">
              <Text>Státusz:&nbsp;</Text>
              <AchievementStatusBadge status={data.status} fontSize="lg" />
            </Flex>

            {(data.status === achievementStatus.ACCEPTED || data.status === achievementStatus.REJECTED) && data.submission ? (
              <>
                <Text>Javító üzenete: data.comment</Text>
                <Text>Pont: {data.submission.score} pont</Text>
              </>
            ) : null}
          </>
        )}
      </Stack>
    </Page>
  )
}
