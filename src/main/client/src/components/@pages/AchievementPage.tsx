import { Box, Button, FormLabel, Heading, Stack, Text, Textarea, Image, useToast, Skeleton } from '@chakra-ui/react'
import { chakra } from '@chakra-ui/system'
import { Page } from '../@layout/Page'
import React, { useEffect, useState, useRef } from 'react'
import { useParams , useNavigate } from 'react-router-dom'
import axios from 'axios'

import { FilePicker } from '../@commons/FilePicker'
import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'
import { Paragraph } from 'components/@commons/Basics'
import { AchievementFullDetailsView, achievementType, achievementStatus } from '../../types/dto/achievements'
import { API_BASE_URL } from 'utils/configurations'

type AchievementPageProps = {}

/*const MOCK_DATA: AchievementFullDetailsView[] = [
  // GET /api/achievement/submit/1
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
    submission: {
      id: 1,
      rejected: false,
      approved: true,
      imageUrlAnswer: 'https://via.placeholder.com/200',
      textAnswer: `Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
        Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
        Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`,
      response: 'Nice work!',
      score: 10
    }
  },
  // GET /api/achievement/submit/2
  {
    achievement: {
      id: 2,
      categoryId: 1,
      title: 'Lorem ipsum2',
      description: 'fdsfdsgsd',
      type: achievementType.TEXT
    },
    status: achievementStatus.REJECTED,
    submission: {
      id: 2,
      rejected: true,
      approved: false,
      textAnswer: `Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
        Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
        Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`,
      response: 'nice try tho',
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
      type: achievementType.BOTH,
      expectedResultDescription: 'Egy kép meg egy izé bigyó'
    },
    status: achievementStatus.NOT_SUBMITTED
  }
]*/

export const AchievementPage: React.FC<AchievementPageProps> = (props) => {
  let [achDetails, setAchDetails] = useState<AchievementFullDetailsView | undefined>(undefined)
  const [textAnswer, setTextAnswer] = useState<string>('')
  const [imageAnswer, setImageAnswer] = useState<File | undefined>(undefined)
  const filePickerRef = useRef<FilePicker>(null)

  const toast = useToast()
  const { id } = useParams()
  const navigate = useNavigate()
  if (!id) {
    return null
  }

  const getAchievementDetails = () => {
    axios.get<AchievementFullDetailsView>(`${API_BASE_URL}/api/achievement/submit/${id}`).then((res) => {
      setAchDetails(res.data)
      if (!achDetails?.achievement) {
        navigate('/bucketlist')
      }
    })
  }

  useEffect(() => {
    getAchievementDetails()
  }, [])

  if (!achDetails) {
    return (
      <Page {...props}>
        <Skeleton height="40px"/>
        <Skeleton marginTop="20px" height="20px"/>
        <Skeleton marginTop="8px" height="20px"/>
        <Skeleton marginTop="8px" height="20px"/>
      </Page>
    )
  }

  // ez csúnya tudom de itt úgyis api hívás lesz
  //achDetails = MOCK_DATA.filter((ach) => ach.achievement?.id === parseInt(id))[0]

  const textAllowed = achDetails.achievement?.type === achievementType.TEXT || achDetails.achievement?.type === achievementType.BOTH
  const imageAllowed = achDetails.achievement?.type === achievementType.IMAGE || achDetails.achievement?.type === achievementType.BOTH
  const submissionAllowed = achDetails?.status === achievementStatus.NOT_SUBMITTED || achDetails?.status === achievementStatus.REJECTED
  const reviewed = achDetails.status === achievementStatus.ACCEPTED || achDetails.status === achievementStatus.REJECTED

  const handleSubmit = () => {
    if (((textAllowed && textAnswer) || (imageAllowed && imageAnswer)) && submissionAllowed) {
      const formData = new FormData()
      if (imageAnswer) {
        formData.append('file', imageAnswer)
      }
      formData.append('achievementId', id)
      formData.append('textAnswer', textAnswer)
      axios
        .post(`${API_BASE_URL}/api/achievement/submit`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then((res) => {
          if (res.data.status === 'OK') {
            toast({
              title: 'Megoldás elküldve',
              status: 'success',
              duration: 5000,
              isClosable: true
            })
            setTextAnswer('')
            if (filePickerRef.current) {
              filePickerRef.current.reset()
            }
            getAchievementDetails()
          } else {
            toast({
              title: res.data.status,
              status: 'error',
              duration: 5000,
              isClosable: true
            })
          }
        })
    } else {
      toast({
        title: 'Üres megoldás',
        description: 'Üres megoldást nem küldhetsz be.',
        status: 'error',
        duration: 5000,
        isClosable: true
      })
    }
  }

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
      <FilePicker
        onFileChange={(fileArray) => setImageAnswer(fileArray[0])}
        placeholder="Csatolt fájl"
        clearButtonLabel="Törlés"
        accept=".png,.jpeg,.jpg,.gif"
        ref={filePickerRef}
      />
    </Box>
  )

  return (
    <Page {...props} loginRequired>
      <Stack>
        <Heading marginBottom="0px">{achDetails.achievement?.title}</Heading>
        <AchievementStatusBadge status={achDetails.status} fontSize="lg" />
        <Paragraph>{achDetails.achievement?.description}</Paragraph>
        {achDetails.achievement?.expectedResultDescription && (
          <Text size="sm">
            <chakra.span fontWeight="bold">Beadandó formátum:</chakra.span>
            &nbsp;{achDetails.achievement?.expectedResultDescription}
          </Text>
        )}
        {achDetails.status !== achievementStatus.NOT_SUBMITTED && (
          <>
            <Heading size="md">Beküldött megoldás</Heading>
            {textAllowed && achDetails.submission && <Paragraph>{achDetails.submission.textAnswer}</Paragraph>}
            {imageAllowed && achDetails.submission && (
              <Box>
                <Image src={achDetails.submission.imageUrlAnswer} alt="Beküldött megoldás" />
              </Box>
            )}
          </>
        )}
        {reviewed && achDetails.submission && (
          <>
            <Heading size="md">Értékelés</Heading>
            <Text>Javító üzenete: {achDetails.submission.response}</Text>
            <Text>Pont: {achDetails.submission.score} pont</Text>
          </>
        )}

        {submissionAllowed && (
          <>
            <Heading size="md">{achDetails.status === achievementStatus.REJECTED ? 'Újra beküldés' : 'Beküldés'}</Heading>
            <Stack>
              {textInput}
              {fileInput}
              <Box>
                <Button mt={4} colorScheme="brand" type="button" onClick={handleSubmit}>
                  Küldés
                </Button>
              </Box>
            </Stack>
          </>
        )}
      </Stack>
    </Page>
  )
}
