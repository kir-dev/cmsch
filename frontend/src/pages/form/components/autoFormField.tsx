import { FormField, FormFieldVariants } from '../../../util/views/form.view'
import { Alert, AlertIcon, Checkbox, Input, Select, Textarea, Text } from '@chakra-ui/react'
import { Control, useController } from 'react-hook-form'
import Markdown from '../../../common-components/Markdown'
import { ReactNode } from 'react'

interface AutoFormFieldProps {
  fieldProps: FormField
  control: Control
  disabled: boolean
  defaultValue: unknown
}

export const AutoFormField = ({ fieldProps, control, disabled, defaultValue }: AutoFormFieldProps) => {
  const selectValues = fieldProps.values.split(',').map((opt) => opt.trim())
  if (!defaultValue) {
    if (fieldProps.type === FormFieldVariants.SELECT) defaultValue = selectValues[0]
  }

  const {
    field,
    fieldState: { error }
  } = useController({
    name: fieldProps.fieldName,
    control,
    defaultValue: defaultValue,
    rules: {
      required: { value: fieldProps.required || fieldProps.type === FormFieldVariants.MUST_AGREE, message: 'Ez a mező kötelező!' },
      pattern: { value: new RegExp(fieldProps.formatRegex), message: 'Ellenőrizze a formátumot!' }
    }
  })
  let component: ReactNode = null
  switch (fieldProps.type) {
    case FormFieldVariants.CHECKBOX:
      component = <Checkbox {...field} isInvalid={!!error} disabled={disabled} defaultChecked={defaultValue === 'true'} />
      break
    case FormFieldVariants.EMAIL:
      component = <Input type="email" {...field} isInvalid={!!error} disabled={disabled} />
      break
    case FormFieldVariants.LONG_TEXT:
      component = <Textarea {...field} isInvalid={!!error} disabled={disabled} />
      break
    case FormFieldVariants.MUST_AGREE:
      component = <Checkbox {...field} isInvalid={!!error} disabled={disabled} defaultChecked={defaultValue === 'true'} />
      break
    case FormFieldVariants.NUMBER:
      component = <Input type="number" {...field} isInvalid={!!error} disabled={disabled} />
      break
    case FormFieldVariants.PHONE:
      component = <Input type="phone" {...field} isInvalid={!!error} disabled={disabled} />
      break
    case FormFieldVariants.SELECT:
      component = (
        <Select {...field} disabled={disabled}>
          {selectValues.map((opt) => (
            <option key={opt} value={opt}>
              {opt}
            </option>
          ))}
        </Select>
      )
      break
    case FormFieldVariants.TEXT:
      component = <Input isInvalid={!!error} type="text" {...field} disabled={disabled} />
      break
    case FormFieldVariants.INFO_BOX:
      component = (
        <Alert variant="left-accent" status="info">
          <AlertIcon />
          <Markdown text={fieldProps.values} />
        </Alert>
      )
      break
    case FormFieldVariants.WARNING_BOX:
      component = (
        <Alert variant="left-accent" status="warning">
          <AlertIcon />
          <Markdown text={fieldProps.values} />
        </Alert>
      )
      break
    case FormFieldVariants.TEXT_BOX:
      component = <Markdown text={fieldProps.values} />
      break
    case FormFieldVariants.SECTION_START:
      component = <Text fontSize={30}>{fieldProps.values}</Text>
      break
  }
  return (
    <>
      {component}
      {error && <Text color="red">{error.message}</Text>}
    </>
  )
}
