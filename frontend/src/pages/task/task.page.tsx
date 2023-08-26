import {
  Alert,
  AlertIcon,
  Badge,
  Box,
  Button,
  Flex,
  FormLabel,
  Heading,
  Image,
  Stack,
  Text,
  Textarea,
  useToast,
  VStack
} from '@chakra-ui/react'
import { chakra } from '@chakra-ui/system'
import { lazy, useEffect, useRef, useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Controller, SubmitHandler, useFieldArray, useForm, useWatch } from 'react-hook-form'
import { Navigate, useParams } from 'react-router-dom'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTaskFullDetailsQuery } from '../../api/hooks/task/useTaskFullDetailsQuery'
import { useTaskSubmissionMutation } from '../../api/hooks/task/useTaskSubmissionMutation'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CustomBreadcrumb } from '../../common-components/CustomBreadcrumb'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { API_BASE_URL } from '../../util/configs/environment.config'
import { stringifyTimeStamp } from '../../util/core-functions.util'
import { l } from '../../util/language'
import { AbsolutePaths } from '../../util/paths'
import { taskFormat, TaskFormatDescriptor, taskStatus, taskType } from '../../util/views/task.view'
import { CustomForm } from './components/CustomForm'
import { FilePicker } from './components/FilePicker'
import { TaskStatusBadge } from './components/TaskStatusBadge'
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

  const component = useConfigContext()?.components.task
  const toast = useToast()
  const { id } = useParams()
  const { setValue, handleSubmit, control } = useForm<FormInput>()
  const { fields, replace, update } = useFieldArray<FormInput>({
    name: 'customForm',
    control
  })
  const customFormData = useWatch({ control, name: 'customForm' })

  const taskSubmissionMutation = useTaskSubmissionMutation()
  const { isLoading, isError, data, isSuccess, refetch } = useTaskFullDetailsQuery(id || 'UNKNOWN')
  useEffect(() => {
    if (!isSuccess && data?.submission && data?.task?.format === taskFormat.CODE) {
      setCodeAnswer(data.submission.textAnswer)
    }
  }, [isSuccess, data])

  if (!id) return <Navigate to={AbsolutePaths.TASKS} />

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const expired = data.task?.availableTo ? data.task?.availableTo < new Date().valueOf() / 1000 : false
  const textAllowed = data.task?.type === taskType.TEXT || data.task?.type === taskType.BOTH
  const fileAllowed = data.task?.type === taskType.IMAGE || data.task?.type === taskType.BOTH || data.task?.type === taskType.ONLY_PDF
  const submissionAllowed =
    (data?.status === taskStatus.NOT_SUBMITTED ||
      data?.status === taskStatus.REJECTED ||
      (component?.resubmissionEnabled && data.status === taskStatus.SUBMITTED)) &&
    !expired
  const reviewed = data.status === taskStatus.ACCEPTED || data.status === taskStatus.REJECTED
  const localSubmission = data?.task?.format === taskFormat.NONE

  const onSubmit: SubmitHandler<FormInput> = (values) => {
    if ((!fileAllowed || fileAnswer) && submissionAllowed) {
      const formData = new FormData()
      formData.append('taskId', id)
      if (fileAnswer) {
        if (fileAnswer.size > 31457280) {
          toast({
            title: l('task-too-large-title'),
            description: l('task-too-large-description'),
            status: 'error',
            isClosable: true
          })
          return
        }
        formData.append('file', fileAnswer)
      }
      if (textAllowed) {
        switch (data.task?.format) {
          case taskFormat.TEXT:
            if (values.textAnswer) {
              formData.append('textAnswer', values.textAnswer)
            } else {
              toast({
                title: l('task-empty-title'),
                description: l('task-empty-description'),
                status: 'error',
                isClosable: true
              })
              return
            }
            break
          case taskFormat.FORM:
            if (customFormData) {
              formData.append(
                'textAnswer',
                customFormData.reduce((acc, current) => acc + current.title + ': ' + current.value.toString() + ` ${current.suffix}\n`, '')
              )
            }
            break
          case taskFormat.CODE:
            if (codeAnswer) {
              formData.append('textAnswer', codeAnswer)
            } else {
              toast({
                title: l('task-empty-title'),
                description: l('task-empty-description'),
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
            fields.forEach((field, idx) => update(idx, { ...field, value: '' }))
            refetch()
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
        title: l('task-empty-title'),
        description: l('task-empty-description'),
        status: 'error',
        isClosable: true
      })
    }
  }

  let textInput = null
  if (textAllowed && data.task) {
    switch (data.task.format) {
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
        textInput = <CustomForm formatDescriptor={data.task.formatDescriptor} control={control} fields={fields} replace={replace} />
        break
      case taskFormat.CODE:
        textInput = <CodeEditor code={codeAnswer} setCode={setCodeAnswer} readonly={false} />
        break
    }
  }

  let submittedText = null
  if (textAllowed && data.submission) {
    submittedText =
      data.task?.format === taskFormat.CODE ? (
        <CodeEditor code={data.submission?.textAnswer} setCode={() => {}} readonly={true} />
      ) : (
        <Text mt={2} whiteSpace="pre-wrap">
          {data.submission.textAnswer}
        </Text>
      )
  }

  const fileInput = fileAllowed && (
    <Box>
      <FormLabel>Csatolt fájl</FormLabel>
      <FilePicker
        onFileChange={(fileArray) => setFileAnswer(fileArray[0])}
        placeholder="Csatolt fájl"
        clearButtonLabel="Törlés"
        accept={data.task?.type === taskType.ONLY_PDF ? '.pdf' : 'image/jpeg,image/png,image/jpg,image/gif'}
        ref={filePickerRef}
      />
    </Box>
  )

  const breadcrumbItems = [
    {
      title: component?.title || 'Feladatok',
      to: AbsolutePaths.TASKS
    },
    {
      title: data.task?.categoryName,
      to: `${AbsolutePaths.TASKS}/category/${data.task?.categoryId}`
    },
    {
      title: data.task?.title
    }
  ]

  return (
    <CmschPage loginRequired>
      <Helmet title={data.task?.title} />
      <CustomBreadcrumb items={breadcrumbItems} />
      <Flex my={5} justify="space-between" flexWrap="wrap" alignItems="center">
        <Box>
          <Heading my={0}>{data.task?.title}</Heading>
        </Box>
        <VStack flex={1} alignItems="end" py={2}>
          <TaskStatusBadge status={data.status} fontSize="lg" />
          {expired && (
            <Box>
              <Badge ml={2} variant="solid" colorScheme="red" fontSize="lg">
                LEJÁRT
              </Badge>
            </Box>
          )}
        </VStack>
      </Flex>
      <Box mt={5}>
        <Markdown text={data.task?.description} />
      </Box>
      {data.task?.expectedResultDescription && (
        <Text size="sm" mt={5}>
          <chakra.span fontWeight="bold">Beadandó formátum:</chakra.span>
          &nbsp;{data.task?.expectedResultDescription}
        </Text>
      )}
      {data.status !== taskStatus.NOT_SUBMITTED && (
        <>
          <Heading size="md" mt={8}>
            Beküldött megoldás
          </Heading>
          {submittedText}
          {fileAllowed && data.submission && (
            <Box>
              {data.submission.imageUrlAnswer && data.submission.imageUrlAnswer.length > 'task/'.length && (
                <Image src={`${API_BASE_URL}/cdn/${data.submission.imageUrlAnswer}`} alt="Beküldött megoldás" />
              )}
              {data.submission.fileUrlAnswer && data.submission.fileUrlAnswer.length > 'task/'.length && (
                <LinkButton href={`${API_BASE_URL}/cdn/${data.submission.fileUrlAnswer}`} external colorScheme="brand" mt={5}>
                  Letöltés
                </LinkButton>
              )}
            </Box>
          )}
        </>
      )}
      {reviewed && data.submission && (
        <>
          <Heading size="md" mt={8}>
            Értékelés
          </Heading>
          <Text mt={2}>Javító üzenete: {data.submission.response}</Text>
          {typeof data.submission.score !== 'undefined' && <Text>Pont: {data.submission.score} pont</Text>}
        </>
      )}

      {data.task?.availableTo && (
        <Alert variant="left-accent" status="info" mt={5}>
          <AlertIcon />A feladat beadási határideje: {stringifyTimeStamp(data.task?.availableTo || 0)}
        </Alert>
      )}

      {submissionAllowed && (
        <>
          <Heading size="md" mt={5}>
            {data.status === taskStatus.REJECTED ? 'Újra beküldés' : 'Beküldés'}
          </Heading>
          <Stack mt={5}>
            {localSubmission ? (
              <Text>Beadás személyesen!</Text>
            ) : (
              <form onSubmit={handleSubmit(onSubmit)}>
                {textInput}
                {fileInput}
                <Flex justifyContent="end" mt={4}>
                  <Button mt={3} colorScheme="brand" type="submit">
                    Küldés
                  </Button>
                </Flex>
              </form>
            )}
          </Stack>
        </>
      )}
    </CmschPage>
  )
}

export default TaskPage
