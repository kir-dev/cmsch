import { Alert, AlertIcon, Box, Button, FormLabel, Heading, Image, Skeleton, Stack, Text, Textarea, useToast } from '@chakra-ui/react'
import { chakra } from '@chakra-ui/system'
import axios from 'axios'
import { CustomBreadcrumb } from 'components/@commons/CustomBreadcrumb'
import { Paragraph } from 'components/@commons/Paragraph'
import { FC, useEffect, useRef, useState } from 'react'
import { Helmet } from 'react-helmet'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { AchievementCategory, AchievementFullDetailsView, achievementStatus, achievementType } from '../../types/dto/achievements'
import { Loading } from '../../utils/Loading'
import { useServiceContext } from '../../utils/useServiceContext'
import { stringifyTimeStamp } from '../../utils/utilFunctions'
import { AchievementStatusBadge } from '../@commons/AchievementStatusBadge'
import { FilePicker } from '../@commons/FilePicker'
import { Page } from '../@layout/Page'

export const AchievementPage: FC = (props) => {
  const [achDetails, setAchDetails] = useState<AchievementFullDetailsView | undefined>(undefined)
  const [textAnswer, setTextAnswer] = useState<string>('')
  const [imageAnswer, setImageAnswer] = useState<File | undefined>(undefined)
  const [categoryName, setCategoryName] = useState<string>('')
  const filePickerRef = useRef<FilePicker>(null)

  const toast = useToast()
  const { id } = useParams()
  const navigate = useNavigate()
  const { throwError } = useServiceContext()

  if (!id) return <Navigate to="/" replace />

  const getAchievementDetails = () => {
    axios
      .get<AchievementFullDetailsView>(`/api/achievement/submit/${id}`)
      .then((res) => {
        if (!res.data.achievement) {
          navigate('/bucketlist')
          toast({
            title: 'Challange nem található',
            description: 'Ilyen challange nem létezik vagy nincs jogosultságod hozzá.',
            status: 'error',
            isClosable: true
          })
        } else {
          setAchDetails(res.data)
          axios
            .get<AchievementCategory>(`/api/achievement/category/${res.data.achievement.categoryId}`)
            .then((res) => {
              setCategoryName(res.data.categoryName || '')
            })
            .catch(() => {
              throwError('Nem sikerült lekérdezni a feladat kategóriáját.', { toast: true })
            })
        }
      })
      .catch(() => {
        throwError('Nem sikerült lekérdezni a Bucketlist feladatot.')
      })
  }

  useEffect(() => {
    getAchievementDetails()
  }, [])

  if (!achDetails) {
    return (
      <Loading>
        <Page {...props}>
          <Skeleton height="40px" />
          <Skeleton marginTop="20px" height="20px" />
          <Skeleton marginTop="8px" height="20px" />
          <Skeleton marginTop="8px" height="20px" />
        </Page>
      </Loading>
    )
  }

  const textAllowed = achDetails.achievement?.type === achievementType.TEXT || achDetails.achievement?.type === achievementType.BOTH
  const imageAllowed = achDetails.achievement?.type === achievementType.IMAGE || achDetails.achievement?.type === achievementType.BOTH
  const submissionAllowed = achDetails?.status === achievementStatus.NOT_SUBMITTED || achDetails?.status === achievementStatus.REJECTED
  const reviewed = achDetails.status === achievementStatus.ACCEPTED || achDetails.status === achievementStatus.REJECTED

  const handleSubmit = () => {
    if (((textAllowed && textAnswer) || (imageAllowed && imageAnswer)) && submissionAllowed) {
      const formData = new FormData()
      if (imageAnswer) {
        if (imageAnswer.size > 31457280) {
          toast({
            title: 'Túl nagy kép',
            description: 'A feltöltött kép túllépte a 30 MB-os feltöltési korlátot!',
            status: 'error',
            isClosable: true
          })
          return
        }
        formData.append('file', imageAnswer)
      }
      formData.append('achievementId', id)
      formData.append('textAnswer', textAnswer)
      axios
        .post(`/api/achievement/submit`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then((res) => {
          if (res.data.status === 'OK') {
            toast({
              title: 'Megoldás elküldve',
              status: 'success',
              isClosable: true
            })
            setTextAnswer('')
            if (filePickerRef.current) {
              filePickerRef.current.reset()
            }
            getAchievementDetails()
            window.scrollTo(0, 0)
          } else {
            toast({
              title: res.data.status,
              status: 'error',
              isClosable: true
            })
          }
        })
        .catch(() => {
          throwError('Hiba történt a beküldés közben.', { toast: true })
        })
    } else {
      toast({
        title: 'Üres megoldás',
        description: 'Üres megoldást nem küldhetsz be.',
        status: 'error',
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
      <FormLabel>Csatolt fájl (max. méret: 30 MB)</FormLabel>
      <FilePicker
        onFileChange={(fileArray) => setImageAnswer(fileArray[0])}
        placeholder="Csatolt fájl"
        clearButtonLabel="Törlés"
        accept="image/jpeg,image/png,image/jpg,image/gif"
        ref={filePickerRef}
      />
    </Box>
  )

  const breadcrumbItems = [
    {
      title: 'Bucketlist',
      to: '/bucketlist'
    },
    {
      title: categoryName,
      to: `/bucketlist/kategoria/${achDetails.achievement?.categoryId}`
    },
    {
      title: achDetails.achievement?.title
    }
  ]

  return (
    <Page {...props} loginRequired groupRequired>
      <Helmet title={achDetails.achievement?.title} />
      <CustomBreadcrumb items={breadcrumbItems} />
      <Heading mb={5}>{achDetails.achievement?.title}</Heading>
      <AchievementStatusBadge status={achDetails.status} fontSize="lg" />
      <Paragraph mt={5}>{achDetails.achievement?.description}</Paragraph>
      {achDetails.achievement?.expectedResultDescription && (
        <Text size="sm" mt={5}>
          <chakra.span fontWeight="bold">Beadandó formátum:</chakra.span>
          &nbsp;{achDetails.achievement?.expectedResultDescription}
        </Text>
      )}
      {achDetails.status !== achievementStatus.NOT_SUBMITTED && (
        <>
          <Heading size="md" mt={8}>
            Beküldött megoldás
          </Heading>
          {textAllowed && achDetails.submission && <Paragraph mt={2}>{achDetails.submission.textAnswer}</Paragraph>}
          {imageAllowed && achDetails.submission && (
            <Box>
              {achDetails.submission.imageUrlAnswer && achDetails.submission.imageUrlAnswer.length > 'achievement/'.length && (
                <Image src={`/cdn/${achDetails.submission.imageUrlAnswer}`} alt="Beküldött megoldás" />
              )}
            </Box>
          )}
        </>
      )}
      {reviewed && achDetails.submission && (
        <>
          <Heading size="md" mt={8}>
            Értékelés
          </Heading>
          <Text mt={2}>Javító üzenete: {achDetails.submission.response}</Text>
          <Text>Pont: {achDetails.submission.score} pont</Text>
        </>
      )}

      {submissionAllowed && (
        <>
          <Heading size="md" mt={8}>
            {achDetails.status === achievementStatus.REJECTED ? 'Újra beküldés' : 'Beküldés'}
          </Heading>
          <Stack mt={5}>
            <Alert variant="left-accent" status="info">
              <AlertIcon />A feladat beadási határideje: {stringifyTimeStamp(achDetails.achievement?.availableTo || 0)}
            </Alert>
            {textInput}
            {fileInput}
            <Box>
              <Button mt={3} colorScheme="brand" type="button" onClick={handleSubmit}>
                Küldés
              </Button>
            </Box>
          </Stack>
        </>
      )}
    </Page>
  )
}
