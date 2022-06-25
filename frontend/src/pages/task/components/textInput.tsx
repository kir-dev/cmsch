import { lazy } from 'react'
import { Box, FormLabel, Input, Textarea } from '@chakra-ui/react'
import { Control, Controller, FieldValues } from 'react-hook-form'
import { taskFormat, TaskFormatDescriptor } from '../../../util/views/task.view'
const CodeEditor = lazy(() => import('./CodeEditor'))

type textInputProps = {
  format: taskFormat | undefined
  formatDescriptor: string | undefined
  control: Control<FieldValues, object>
}

export const TextInput = ({ format, formatDescriptor, control }: textInputProps) => {
  switch (format) {
    case taskFormat.TEXT:
      return (
        <Box mt={5}>
          <FormLabel htmlFor="textAnswer">Szöveges válasz</FormLabel>
          <Controller
            name="textAnswer"
            control={control}
            render={({ field }) => <Textarea id="textAnswer" placeholder="Szöveges válasz" {...field} />}
          />
        </Box>
      )
    case taskFormat.FORM:
      const fields: TaskFormatDescriptor[] = JSON.parse(formatDescriptor!!)
      return (
        <>
          {fields.map((f, idx) => (
            <Box mt={5} key={f.title}>
              <FormLabel htmlFor={`customField${idx}`}>{f.title}</FormLabel>
              <Controller
                name={`customField${idx}`}
                control={control}
                render={({ field }) =>
                  f.type === 'textarea' ? (
                    <Textarea id={`customField${idx}`} placeholder={f.title} {...field} />
                  ) : (
                    <Input id={`customField${idx}`} placeholder={f.title} {...field} type={f.type} />
                  )
                }
              />
            </Box>
          ))}
        </>
      )
    case taskFormat.CODE:
      return <CodeEditor />
    default:
      return null
  }
}
