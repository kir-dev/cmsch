import { Box, FormLabel, Textarea, Input } from '@chakra-ui/react'
import { useEffect } from 'react'
import { useFieldArray, Controller, Control, FieldValues } from 'react-hook-form'
import { TaskFormatDescriptor } from '../../../util/views/task.view'
import { FormInput } from '../task.page'

export type CustomFormProps = {
  formatDescriptor: string | undefined
  control: Control<FieldValues, object>
}

export const CustomForm = ({ formatDescriptor, control }: CustomFormProps) => {
  const { fields, replace } = useFieldArray<FormInput, 'customForm', 'id'>({
    name: 'customForm',
    control
  })
  useEffect(() => {
    if (formatDescriptor) {
      const inputFields: TaskFormatDescriptor[] = JSON.parse(formatDescriptor)
      replace(inputFields.map((field) => ({ value: '', title: field.title, type: field.type, suffix: field.suffix })))
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
