import { Alert, AlertIcon, Badge, Box, Button, Flex, FormLabel, Heading, Image, Stack, Text, Textarea, useToast } from '@chakra-ui/react'
import { chakra } from '@chakra-ui/system'
import { useRef, useState, lazy } from 'react'
import { Helmet } from 'react-helmet'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { Controller, SubmitHandler, useForm, useWatch } from 'react-hook-form'
import { taskFormat, TaskFormatDescriptor, taskStatus, taskType } from '../../util/views/task.view'
import { FilePicker } from './components/FilePicker'
import { Loading } from '../../common-components/Loading'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { Paragraph } from '../../common-components/Paragraph'
import { TaskStatusBadge } from './components/TaskStatusBadge'
import { stringifyTimeStamp } from '../../util/core-functions.util'
import { TaskDetailsSkeleton } from './components/taskDetailsSkeleton'
import { useTaskFullDetailsQuery } from '../../api/hooks/useTaskFullDetailsQuery'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { CustomForm } from './components/CustomForm'
import { useTaskSubmissionMutation } from '../../api/hooks/useTaskSubmissionMutation'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { useEffect } from 'react'
import { taskSubmissionResponseMap } from './util/taskSubmissionResponseMap'
const CodeEditor = lazy(() => import('./components/CodeEditor'))

export interface FormInput {
  textAnswer?: string
  fileAnswer?: File
  customForm?: ({
    value: string | number
  } & TaskFormatDescriptor)[]
}

const TaskPage = () => {
  const [fileAnswer, setFileAnswer] = useState<File | undefined>(undefined)
  const filePickerRef = useRef<FilePicker>(null)
  const [codeAnswer, setCodeAnswer] = useState<string>(`#include <stdio.h>\nint main() {\n  printf("Hello, World!");\n  return 0;\n}`)

  const taskConfig = useConfigContext()?.components.task
  const toast = useToast()
  const { id } = useParams()
  const navigate = useNavigate()
  const { setValue, handleSubmit, control } = useForm<FormInput>()
  const customFormData = useWatch({ control, name: 'customForm' })

  if (!id) return <Navigate to="/" replace />

  const taskSubmissionMutation = useTaskSubmissionMutation()
  const taskDetailsQuery = useTaskFullDetailsQuery(id, () => {
    navigate('/tasks')
    toast({
      title: 'Feladat nem található',
      description: 'Ilyen feladat nem létezik vagy nincs jogosultságod hozzá.',
      status: 'error',
      isClosable: true
    })
  })
  useEffect(() => {
    if (taskDetailsQuery.isSuccess && taskDetailsQuery.data.submission && taskDetailsQuery.data.task?.format === taskFormat.CODE) {
      setCodeAnswer(taskDetailsQuery.data.submission.textAnswer)
    }
  }, [taskDetailsQuery.status])

  if (taskDetailsQuery.isSuccess) {
    const taskDetails = taskDetailsQuery.data
    const expired = taskDetails.task?.availableTo ? taskDetails.task?.availableTo < new Date().valueOf() / 1000 : false
    const textAllowed = taskDetails.task?.type === taskType.TEXT || taskDetails.task?.type === taskType.BOTH
    const fileAllowed =
      taskDetails.task?.type === taskType.IMAGE || taskDetails.task?.type === taskType.BOTH || taskDetails.task?.type === taskType.ONLY_PDF
    const submissionAllowed = (taskDetails?.status === taskStatus.NOT_SUBMITTED || taskDetails?.status === taskStatus.REJECTED) && !expired
    const reviewed = taskDetails.status === taskStatus.ACCEPTED || taskDetails.status === taskStatus.REJECTED

    const onSubmit: SubmitHandler<FormInput> = (data) => {
      if ((!fileAllowed || fileAnswer) && submissionAllowed) {
        const formData = new FormData()
        formData.append('taskId', id)
        if (fileAnswer) {
          if (fileAnswer.size > 31457280) {
            toast({
              title: 'Túl nagy fájl',
              description: 'A feltöltött fájl túllépte a 30 MB-os feltöltési korlátot!',
              status: 'error',
              isClosable: true
            })
            return
          }
          formData.append('file', fileAnswer)
        }
        if (textAllowed) {
          switch (taskDetails.task?.format) {
            case taskFormat.TEXT:
              if (data.textAnswer) {
                formData.append('textAnswer', data.textAnswer)
              } else {
                toast({
                  title: 'Üres megoldás',
                  description: 'Üres megoldást nem küldhetsz be.',
                  status: 'error',
                  isClosable: true
                })
                return
              }
              break
            case taskFormat.FORM:
              if (customFormData) {
                if (customFormData.some((field) => field.value === '')) {
                  toast({
                    title: 'Hiányos megoldás',
                    description: 'Töltsd ki az összes mezőt!',
                    status: 'error',
                    isClosable: true
                  })
                  return
                }
                formData.append(
                  'textAnswer',
                  customFormData.reduce((acc, current) => acc + current.title + ': ' + current.value.toString() + current.suffix, '')
                )
              }
              break
            case taskFormat.CODE:
              if (codeAnswer) {
                formData.append('textAnswer', codeAnswer)
              } else {
                toast({
                  title: 'Üres megoldás',
                  description: 'Üres megoldást nem küldhetsz be.',
                  status: 'error',
                  isClosable: true
                })
                return
              }
          }
        }

        taskSubmissionMutation.mutate(formData, {
          onSuccess: (result) => {
            if (result.status === 'OK') {
              toast({
                title: 'Megoldás elküldve',
                status: 'success',
                isClosable: true
              })
              setValue('textAnswer', '')
              if (filePickerRef.current) {
                filePickerRef.current.reset()
              }
              taskDetailsQuery.refetch()
              window.scrollTo(0, 0)
            } else {
              toast({
                title: taskSubmissionResponseMap.get(result.status),
                status: 'error',
                isClosable: true
              })
            }
          },
          onError: (error) => {
            toast({
              title: error.message || 'Hiba a megoldása elküldése közben',
              status: 'error',
              isClosable: true
            })
          }
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

    let textInput = null
    if (textAllowed && taskDetails.task) {
      switch (taskDetails.task.format) {
        case taskFormat.TEXT:
          textInput = (
            <Box mt={5}>
              <FormLabel htmlFor="textAnswer">Szöveges válasz</FormLabel>
              <Controller
                name="textAnswer"
                control={control}
                render={({ field }) => <Textarea id="textAnswer" placeholder="Szöveges válasz" {...field} />}
              />
            </Box>
          )
          break
        case taskFormat.FORM:
          textInput = <CustomForm formatDescriptor={taskDetails.task.formatDescriptor} control={control} />
          break
        case taskFormat.CODE:
          textInput = <CodeEditor code={codeAnswer} setCode={setCodeAnswer} readonly={false} />
          break
      }
    }

    let submittedText = null
    if (textAllowed && taskDetails.submission) {
      submittedText =
        taskDetails.task?.format === taskFormat.CODE ? (
          <CodeEditor code={taskDetails.submission?.textAnswer} setCode={() => {}} readonly={true} />
        ) : (
          <Paragraph mt={2}>{taskDetails.submission.textAnswer}</Paragraph>
        )
    }

    const fileInput = fileAllowed && (
      <Box>
        <FormLabel>Csatolt fájl (max. méret: 30 MB)</FormLabel>
        <FilePicker
          onFileChange={(fileArray) => setFileAnswer(fileArray[0])}
          placeholder="Csatolt fájl"
          clearButtonLabel="Törlés"
          accept={taskDetails.task?.type === taskType.ONLY_PDF ? '.pdf' : 'image/jpeg,image/png,image/jpg,image/gif'}
          ref={filePickerRef}
        />
      </Box>
    )

    const breadcrumbItems = [
      {
        title: taskConfig?.title || 'Bucketlist',
        to: '/tasks'
      },
      {
        title: taskDetails.task?.categoryName,
        to: `/tasks/category/${taskDetails.task?.categoryId}`
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
        <Flex>
          <TaskStatusBadge status={taskDetails.status} fontSize="lg" />
          {expired && (
            <Badge ml={2} variant="solid" colorScheme="red" fontSize="lg">
              LEJÁRT
            </Badge>
          )}
        </Flex>
        <Box mt={5}>
          <Markdown text={taskDetails.task?.description} />
        </Box>
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
            {submittedText}
            {fileAllowed && taskDetails.submission && (
              <Box>
                {taskDetails.submission.imageUrlAnswer && taskDetails.submission.imageUrlAnswer.length > 'task/'.length && (
                  <Image src={`${API_BASE_URL}/cdn/${taskDetails.submission.imageUrlAnswer}`} alt="Beküldött megoldás" />
                )}
                {taskDetails.submission.fileUrlAnswer && taskDetails.submission.fileUrlAnswer.length > 'task/'.length && (
                  <LinkButton href={`${API_BASE_URL}/cdn/${taskDetails.submission.fileUrlAnswer}`} external colorScheme="brand" mt={5}>
                    Letöltés
                  </LinkButton>
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
              <form onSubmit={handleSubmit(onSubmit)}>
                <Alert variant="left-accent" status="info">
                  <AlertIcon />A feladat beadási határideje: {stringifyTimeStamp(taskDetails.task?.availableTo || 0)}
                </Alert>
                {textInput}
                {fileInput}
                <Box>
                  <Button mt={3} colorScheme="brand" type="submit">
                    Küldés
                  </Button>
                </Box>
              </form>
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
