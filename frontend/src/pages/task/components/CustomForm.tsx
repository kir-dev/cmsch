import { Box, FormLabel, Textarea, Input, useToast } from '@chakra-ui/react'
import { useEffect } from 'react'
import { useFieldArray, Controller, Control, FieldValues } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { TaskFormatDescriptor } from '../../../util/views/task.view'
import { FormInput } from '../task.page'
import { AbsolutePaths } from '../../../util/paths'

type CustomFormProps = {
  formatDescriptor: string | undefined
  control: Control<FieldValues, object>
}

export const CustomForm = ({ formatDescriptor, control }: CustomFormProps) => {
  const toast = useToast()
  const navigate = useNavigate()
  const { fields, replace } = useFieldArray<FormInput>({
    name: 'customForm',
    control
  })
  useEffect(() => {
    if (formatDescriptor) {
      try {
        const inputFields: TaskFormatDescriptor[] = JSON.parse(formatDescriptor)
        replace(inputFields.map((field) => ({ value: '', ...field })))
      } catch (e) {
        toast({
          title: 'Érvénytelen feladat',
          description: 'A feladat űrlapjának formátuma érvénytelen.',
          status: 'error',
          isClosable: true
        })
        navigate(AbsolutePaths.TASKS)
      }
    }
  }, [])
  return (
    <>
      {fields.map((f, idx) => (
        <Box mt={5} key={f.id}>
          <FormLabel htmlFor={`customForm.${idx}.value`}>{f.title}</FormLabel>
          <Controller
            name={`customForm.${idx}.value`}
            control={control}
            render={({ field }) =>
              f.type === 'textarea' ? (
                <Textarea id={`customForm.${idx}.value`} placeholder={f.title} {...field} />
              ) : (
                <Input id={`customForm.${idx}.value`} placeholder={f.title} {...field} type={f.type} />
              )
            }
          />
        </Box>
      ))}
    </>
  )
}
