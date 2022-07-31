import { Box, FormLabel, Input, Textarea, useToast } from '@chakra-ui/react'
import { useEffect } from 'react'
import { Control, Controller, FieldArrayWithId, UseFieldArrayReplace } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { TaskFormatDescriptor } from '../../../util/views/task.view'
import { FormInput } from '../task.page'
import { AbsolutePaths } from '../../../util/paths'
import { InputWithAddon } from './InputWithAddon'

type CustomFormProps = {
  formatDescriptor: string | undefined
  control: Control<FormInput, object>
  fields: FieldArrayWithId<FormInput, 'customForm', 'id'>[]
  replace: UseFieldArrayReplace<FormInput, 'customForm'>
}

export const CustomForm = ({ formatDescriptor, control, fields, replace }: CustomFormProps) => {
  const toast = useToast()
  const navigate = useNavigate()

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
            render={({ field }) => (
              <InputWithAddon suffix={f.suffix}>
                {f.type === 'textarea' ? (
                  <Textarea id={`customForm.${idx}.value`} placeholder={f.title} {...field} />
                ) : (
                  <Input id={`customForm.${idx}.value`} placeholder={f.title} {...field} type={f.type} />
                )}
              </InputWithAddon>
            )}
          />
        </Box>
      ))}
    </>
  )
}
