import { Alert, AlertIcon, Box, Button, FormLabel, Heading, Image, Stack, Text, Textarea, useToast } from '@chakra-ui/react'
import { chakra } from '@chakra-ui/system'
import axios from 'axios'
import { useRef, useState } from 'react'
import { Helmet } from 'react-helmet'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { taskStatus, taskType } from '../../util/views/task.view'
import { FilePicker } from './components/FilePicker'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { Paragraph } from '../../common-components/Paragraph'
import { TaskStatusBadge } from './components/TaskStatusBadge'
import { stringifyTimeStamp } from '../../util/core-functions.util'
import { TaskDetailsSkeleton } from './components/taskDetailsSkeleton'
import { useTaskFullDetailsQuery } from '../../api/hooks/useTaskFullDetailsQuery'

const TaskPage = () => {
  const [textAnswer, setTextAnswer] = useState<string>('')
  const [imageAnswer, setImageAnswer] = useState<File | undefined>(undefined)
  const filePickerRef = useRef<FilePicker>(null)

  const toast = useToast()
  const { id } = useParams()
  const navigate = useNavigate()

  if (!id) return <Navigate to="/" replace />

  const taskDetailsQuery = useTaskFullDetailsQuery(id, () => {
    navigate('/bucketlist')
    toast({
      title: 'Challange nem található',
      description: 'Ilyen challange nem létezik vagy nincs jogosultságod hozzá.',
      status: 'error',
      isClosable: true
    })
  })

  if (taskDetailsQuery.isSuccess) {
    const taskDetails = taskDetailsQuery.data
    const textAllowed = taskDetails.task?.type === taskType.TEXT || taskDetails.task?.type === taskType.BOTH
    const imageAllowed = taskDetails.task?.type === taskType.IMAGE || taskDetails.task?.type === taskType.BOTH
    const submissionAllowed = taskDetails?.status === taskStatus.NOT_SUBMITTED || taskDetails?.status === taskStatus.REJECTED
    const reviewed = taskDetails.status === taskStatus.ACCEPTED || taskDetails.status === taskStatus.REJECTED

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
        formData.append('taskId', id)
        formData.append('textAnswer', textAnswer)
        axios
          .post(`/api/task/submit`, formData, {
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
              taskDetailsQuery.refetch()
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
            console.error('Hiba történt a beküldés közben.', { toast: true })
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
        title: taskDetails.task?.categoryName,
        to: `/bucketlist/kategoria/${taskDetails.task?.categoryId}`
      },
      {
        title: taskDetails.task?.title
      }
    ]

    return (
      <CmschPage loginRequired groupRequired>
        <Helmet title={taskDetails.task?.title} />
        <CustomBreadcrumb items={breadcrumbItems} />
        <Heading mb={5}>{taskDetails.task?.title}</Heading>
        <TaskStatusBadge status={taskDetails.status} fontSize="lg" />
        <Paragraph mt={5}>{taskDetails.task?.description}</Paragraph>
        {taskDetails.task?.expectedResultDescription && (
          <Text size="sm" mt={5}>
            <chakra.span fontWeight="bold">Beadandó formátum:</chakra.span>
            &nbsp;{taskDetails.task?.expectedResultDescription}
          </Text>
        )}
        {taskDetails.status !== taskStatus.NOT_SUBMITTED && (
          <>
            <Heading size="md" mt={8}>
              Beküldött megoldás
            </Heading>
            {textAllowed && taskDetails.submission && <Paragraph mt={2}>{taskDetails.submission.textAnswer}</Paragraph>}
            {imageAllowed && taskDetails.submission && (
              <Box>
                {taskDetails.submission.imageUrlAnswer && taskDetails.submission.imageUrlAnswer.length > 'task/'.length && (
                  <Image src={`/cdn/${taskDetails.submission.imageUrlAnswer}`} alt="Beküldött megoldás" />
                )}
              </Box>
            )}
          </>
        )}
        {reviewed && taskDetails.submission && (
          <>
            <Heading size="md" mt={8}>
              Értékelés
            </Heading>
            <Text mt={2}>Javító üzenete: {taskDetails.submission.response}</Text>
            <Text>Pont: {taskDetails.submission.score} pont</Text>
          </>
        )}

        {submissionAllowed && (
          <>
            <Heading size="md" mt={8}>
              {taskDetails.status === taskStatus.REJECTED ? 'Újra beküldés' : 'Beküldés'}
            </Heading>
            <Stack mt={5}>
              <Alert variant="left-accent" status="info">
                <AlertIcon />A feladat beadási határideje: {stringifyTimeStamp(taskDetails.task?.availableTo || 0)}
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
      </CmschPage>
    )
  } else {
    return (
      <Loading>
        <TaskDetailsSkeleton />
      </Loading>
    )
  }
}

export default TaskPage
