import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTaskFullDetailsQuery } from '@/api/hooks/task/useTaskFullDetailsQuery'
import { useTaskSubmissionMutation } from '@/api/hooks/task/useTaskSubmissionMutation'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CustomBreadcrumb } from '@/common-components/CustomBreadcrumb'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { useToast } from '@/hooks/use-toast'
import { stringifyTimeStamp } from '@/util/core-functions.util.ts'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import { TaskFormat, type TaskFormatDescriptor, TaskStatus, TaskType } from '@/util/views/task.view'
import { Info } from 'lucide-react'
import { lazy, useEffect, useRef, useState } from 'react'
import { Controller, type SubmitHandler, useFieldArray, useForm, useWatch } from 'react-hook-form'
import { Navigate, useParams } from 'react-router'
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

const getAcceptedFileType = (type?: TaskType) => {
  if (type === TaskType.ONLY_ZIP) return '.zip'
  else if (type === TaskType.ONLY_PDF) return '.pdf'
  else return 'image/jpeg,image/png,image/jpg,image/gif,image/webp'
}
const TaskPage = () => {
  const [fileAnswer, setFileAnswer] = useState<File | undefined>(undefined)
  const filePickerRef = useRef<FilePicker>(null)
  const [codeAnswer, setCodeAnswer] = useState<string>(`#include <stdio.h>\nint main() {\n  printf("Hello, World!");\n  return 0;\n}`)

  const component = useConfigContext()?.components?.task

  const { toast } = useToast()
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
    if (!isSuccess && data?.submission && data?.task?.format === TaskFormat.CODE) {
      setCodeAnswer(data.submission.textAnswer)
    }
  }, [isSuccess, data])

  if (!id) return <Navigate to={AbsolutePaths.TASKS} />

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const expired = data.task?.availableTo ? data.task?.availableTo < new Date().valueOf() / 1000 : false
  const textAllowed = data.task?.type === TaskType.TEXT || data.task?.type === TaskType.BOTH
  const fileAllowed =
    data.task?.type === TaskType.IMAGE ||
    data.task?.type === TaskType.BOTH ||
    data.task?.type === TaskType.ONLY_PDF ||
    data.task?.type === TaskType.ONLY_ZIP

  const submissionAllowed =
    (data?.status === TaskStatus.NOT_SUBMITTED ||
      data?.status === TaskStatus.REJECTED ||
      (component?.resubmissionEnabled && data.status === TaskStatus.SUBMITTED)) &&
    !expired
  const reviewed = data.status === TaskStatus.ACCEPTED || data.status === TaskStatus.REJECTED
  const localSubmission = data?.task?.format === TaskFormat.NONE

  const onSubmit: SubmitHandler<FormInput> = async (values) => {
    if ((!fileAllowed || fileAnswer) && submissionAllowed) {
      const formData = new FormData()
      formData.append('taskId', id)
      if (fileAnswer) {
        if (fileAnswer.size > 31457280) {
          toast({
            title: l('task-too-large-title'),
            description: l('task-too-large-description'),
            variant: 'destructive'
          })
          return
        }
        try {
          const processed = await processImageForUpload(fileAnswer)
          if (processed === null) {
            toast({
              title: 'Fájl túl nagy',
              description: 'Animált fájlok maximum 2 MB méretűek lehetnek. Próbáld meg .WEBP-be konvertálni.',
              variant: 'destructive'
            })
            return
          }
          formData.append('file', processed)
        } catch (e: unknown) {
          toast({
            title: 'Kép feldolgozási hiba',
            description: (e as Error)?.message || 'Nem sikerült a kép optimalizálása.',
            variant: 'destructive'
          })
          return
        }
      }
      if (textAllowed) {
        switch (data.task?.format) {
          case TaskFormat.TEXT:
            if (values.textAnswer) {
              formData.append('textAnswer', values.textAnswer)
            } else {
              toast({
                title: l('task-empty-title'),
                description: l('task-empty-description'),
                variant: 'destructive'
              })
              return
            }
            break
          case TaskFormat.FORM:
            if (customFormData) {
              formData.append(
                'textAnswer',
                customFormData.reduce((acc, current) => acc + current.title + ': ' + current.value.toString() + ` ${current.suffix}\n`, '')
              )
            }
            break
          case TaskFormat.CODE:
            if (codeAnswer) {
              formData.append('textAnswer', codeAnswer)
            } else {
              toast({
                title: l('task-empty-title'),
                description: l('task-empty-description'),
                variant: 'destructive'
              })
              return
            }
        }
      }

      taskSubmissionMutation.mutate(formData, {
        onSuccess: (result) => {
          if (result.status === 'OK') {
            toast({
              title: 'Megoldás elküldve'
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
              variant: 'destructive'
            })
          }
        },
        onError: (error) => {
          toast({
            title: error.message || 'Hiba a megoldása elküldése közben',
            variant: 'destructive'
          })
        }
      })
    } else {
      toast({
        title: l('task-empty-title'),
        description: l('task-empty-description'),
        variant: 'destructive'
      })
    }
  }

  let textInput = null
  if (textAllowed && data.task) {
    switch (data.task.format) {
      case TaskFormat.TEXT:
        textInput = (
          <div className="mt-5 flex flex-col gap-2">
            <Label htmlFor="textAnswer">Szöveges válasz</Label>
            <Controller
              name="textAnswer"
              control={control}
              render={({ field }) => <Textarea id="textAnswer" placeholder="Szöveges válasz" {...field} />}
            />
          </div>
        )
        break
      case TaskFormat.FORM:
        textInput = <CustomForm formatDescriptor={data.task.formatDescriptor} control={control} fields={fields} replace={replace} />
        break
      case TaskFormat.CODE:
        textInput = <CodeEditor code={codeAnswer} setCode={setCodeAnswer} readonly={false} />
        break
    }
  }

  let submittedText = null
  if (textAllowed && data.submission) {
    submittedText =
      data.task?.format === TaskFormat.CODE ? (
        <CodeEditor code={data.submission?.textAnswer} setCode={() => {}} readonly={true} />
      ) : (
        <p className="mt-2 whitespace-pre-wrap">{data.submission.textAnswer}</p>
      )
  }

  const fileInput = fileAllowed && (
    <div className="flex flex-col gap-2">
      <Label>Csatolt fájl</Label>
      <FilePicker
        onFileChange={(fileArray) => setFileAnswer(fileArray[0])}
        placeholder="Csatolt fájl"
        clearButtonLabel="Törlés"
        accept={getAcceptedFileType(data.task?.type)}
        ref={filePickerRef}
      />
    </div>
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
    <CmschPage loginRequired={true} title={data.task?.title}>
      <CustomBreadcrumb items={breadcrumbItems} />
      <div className="my-5 flex flex-wrap items-center justify-between">
        <div>
          <h1 className="my-0 text-4xl font-bold tracking-tight">{data.task?.title}</h1>
        </div>
        <div className="flex flex-1 flex-col items-end py-2 gap-2">
          <TaskStatusBadge status={data.status} />
          {expired && (
            <div>
              <Badge className="ml-2" variant="destructive">
                LEJÁRT
              </Badge>
            </div>
          )}
        </div>
      </div>
      <div className="mt-5">
        <Markdown text={data.task?.description} />
      </div>
      {data.task?.expectedResultDescription && (
        <p className="mt-5 text-sm">
          <span className="font-bold">Beadandó formátum:</span>
          &nbsp;{data.task?.expectedResultDescription}
        </p>
      )}
      {data.status !== TaskStatus.NOT_SUBMITTED && (
        <>
          <h2 className="mt-8 text-2xl font-bold">Beküldött megoldás</h2>
          {submittedText}
          {fileAllowed && data.submission && (
            <div>
              {data.submission.imageUrlAnswer && (
                <img src={data.submission.imageUrlAnswer} alt="Beküldött megoldás" className="max-w-full" />
              )}
              {data.submission.fileUrlAnswer && (
                <LinkButton href={data.submission.fileUrlAnswer} external className="mt-5">
                  Letöltés
                </LinkButton>
              )}
            </div>
          )}
        </>
      )}
      {reviewed && data.submission && (
        <>
          <h2 className="mt-8 text-2xl font-bold">Értékelés</h2>
          <p className="mt-2">Javító üzenete: {data.submission.response}</p>
          {typeof data.submission.score !== 'undefined' && <p>Pont: {data.submission.score} pont</p>}
        </>
      )}

      {data.task?.availableTo && (
        <Alert className="mt-5 border-l-4">
          <Info className="h-4 w-4" />
          <AlertTitle>Határidő</AlertTitle>
          <AlertDescription>A feladat beadási határideje: {stringifyTimeStamp(data.task?.availableTo || 0)}</AlertDescription>
        </Alert>
      )}

      {submissionAllowed && (
        <>
          <h2 className="mt-5 text-2xl font-bold">{data.status === TaskStatus.REJECTED ? 'Újra beküldés' : 'Beküldés'}</h2>
          <div className="mt-5 flex flex-col gap-5">
            {localSubmission ? (
              <p>Beadás személyesen!</p>
            ) : (
              // eslint-disable-next-line react-hooks/refs
              <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-5">
                {textInput}
                {fileInput}
                <div className="mt-4 flex justify-end">
                  <Button type="submit">Küldés</Button>
                </div>
              </form>
            )}
          </div>
        </>
      )}
    </CmschPage>
  )
}

export default TaskPage

// null when the image was too big
async function processImageForUpload(file: File, maxWidth = 1920, maxHeight = 1920, quality = 0.85): Promise<File | null> {
  const maxBlobSize = 1024 * 1024 * 3

  const type = file.type.toLowerCase()
  if (!(type === 'image/jpeg' || type === 'image/jpg' || type === 'image/png' || type === 'image/gif' || type === 'image/webp')) {
    return file
  }

  // GIF: can't optimize, only allow small uploads
  if (type === 'image/gif') {
    if (file.size > maxBlobSize) return null
    return file
  }

  // WEBP: can't optimize animated webp, only allow small uploads
  if (type === 'image/webp') {
    const isAnim = await isAnimatedWebP(file)
    if (isAnim) {
      if (file.size > maxBlobSize) return null
      return file
    }
  }

  const dataUrl = await new Promise<string>((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = (e) => reject(e)
    reader.readAsDataURL(file)
  })

  const img = await new Promise<HTMLImageElement>((resolve, reject) => {
    const image = document.createElement('img')
    image.onload = () => resolve(image)
    image.onerror = (e) => reject(e)
    image.src = dataUrl
  })

  // Compute new dimensions keeping aspect ratio
  const { width, height } = img
  const scale = Math.min(maxWidth / width, maxHeight / height, 1)
  const targetW = Math.floor(width * scale)
  const targetH = Math.floor(height * scale)

  // If no need to resize and file is not too big, we can still compress JPEG/PNG
  const canvas = document.createElement('canvas')
  canvas.width = targetW
  canvas.height = targetH
  const ctx = canvas.getContext('2d')
  if (!ctx) return file
  ctx.imageSmoothingEnabled = true
  ctx.imageSmoothingQuality = 'high'
  ctx.drawImage(img, 0, 0, targetW, targetH)

  // Safari doesn't support canvas.toBlob() with WEBP
  // https://developer.mozilla.org/en-US/docs/Web/API/HTMLCanvasElement/toBlob#browser_compatibility
  const isSafari = detectSafari()
  const outputType = isSafari ? 'image/jpeg' : 'image/webp'

  const blob: Blob = await new Promise((resolve) => canvas.toBlob((b) => resolve(b || file), outputType, quality))
  if (!blob) return file

  const name = file.name.replace(/\.(jpg|jpeg|png|gif|webp)$/i, outputType === 'image/webp' ? '.webp' : '.jpg')
  return new File([blob], name, { type: outputType, lastModified: Date.now() })
}

function detectSafari(): boolean {
  const ua = navigator.userAgent
  const isSafari = /^((?!chrome|android).)*safari/i.test(ua)
  // Also consider iOS Safari where Chrome uses Safari engine
  const isIOS = /(ipad|iphone|ipod)/i.test(ua)
  return isSafari || isIOS
}

async function isAnimatedWebP(file: File): Promise<boolean> {
  // Parse minimal RIFF to check for ANIM chunk
  const buffer = await file.slice(0, 512 * 1024).arrayBuffer()
  const bytes = new Uint8Array(buffer)
  // Look for 'ANIM' or 'ANMF' chunk tags which indicate animation
  const text = new TextDecoder('ascii').decode(bytes)
  return text.includes('ANIM') || text.includes('ANMF')
}
