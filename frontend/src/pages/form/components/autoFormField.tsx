import { Alert, AlertIcon, Checkbox, Flex, FormLabel, Input, Select, Text, Textarea, useColorModeValue } from '@chakra-ui/react'
import type { ReactNode } from 'react'
import { type Control, useController } from 'react-hook-form'
import { useStyle } from '../../../api/contexts/config/ConfigContext.tsx'
import Markdown from '../../../common-components/Markdown'
import { VotingField } from '../../../common-components/VotingField'
import { isCheckbox, isGridField } from '../../../util/core-functions.util'
import { type FormField, FormFieldVariants, type VotingFieldOption } from '../../../util/views/form.view'
import { GridField } from './GridField'

interface AutoFormFieldProps {
  fieldProps: FormField
  control: Control
  disabled: boolean
  submittedValue?: string
}

export const AutoFormField = ({ fieldProps, control, disabled, submittedValue }: AutoFormFieldProps) => {
  const selectValues = fieldProps.values.split(',').map((opt) => opt.trim())
  let defaultValue = isCheckbox(fieldProps.type) ? fieldProps.defaultValue === 'true' : fieldProps.defaultValue
  let requiredValue = fieldProps.required

  const style = useStyle()
  const checkboxBorderColor = useColorModeValue(style?.lightTextColor, style?.darkTextColor) ?? '#888'
  if (submittedValue) {
    if (isCheckbox(fieldProps.type)) defaultValue = submittedValue === 'true'
    else if (isGridField(fieldProps.type)) defaultValue = JSON.parse(submittedValue)
    else defaultValue = submittedValue
  } else if (!defaultValue) {
    if (fieldProps.type === FormFieldVariants.SELECT) defaultValue = selectValues[0]
    else defaultValue = ''
  }

  if (fieldProps.type.startsWith('INJECT_')) {
    requiredValue = false
  }

  const {
    field,
    fieldState: { error }
  } = useController({
    name: fieldProps.fieldName,
    control,
    defaultValue: defaultValue,
    rules: {
      required: {
        value: requiredValue || fieldProps.type === FormFieldVariants.MUST_AGREE,
        message: 'Ez a mező kötelező!'
      },
      pattern: { value: new RegExp(fieldProps.formatRegex), message: 'Ellenőrizze a formátumot!' }
    }
  })
  let component: ReactNode = null
  switch (fieldProps.type) {
    case FormFieldVariants.CHECKBOX:
      component = (
        <Flex alignItems="center" mt={10}>
          <Checkbox {...field} isInvalid={!!error} borderColor={checkboxBorderColor} disabled={disabled} defaultChecked={!!defaultValue}>
            <Text fontSize={20} ml={3}>
              {fieldProps.label}
            </Text>
          </Checkbox>
        </Flex>
      )
      break
    case FormFieldVariants.EMAIL:
      component = <Input type="email" {...field} isInvalid={!!error} _placeholder={{ color: 'inherit' }} disabled={disabled} />
      break
    case FormFieldVariants.LONG_TEXT:
      component = <Textarea {...field} isInvalid={!!error} disabled={disabled} />
      break
    case FormFieldVariants.MUST_AGREE:
      component = (
        <Flex alignItems="center" my={10}>
          <Checkbox {...field} isInvalid={!!error} borderColor={checkboxBorderColor} disabled={disabled} defaultChecked={!!defaultValue} />
          <FormLabel ml={3} mb={0} fontSize={20} htmlFor={fieldProps.fieldName}>
            {fieldProps.label}
          </FormLabel>
        </Flex>
      )
      break
    case FormFieldVariants.DATE:
      component = <Input type="date" {...field} isInvalid={!!error} _placeholder={{ color: 'inherit' }} disabled={disabled} />
      break
    case FormFieldVariants.NUMBER:
      component = <Input type="number" {...field} isInvalid={!!error} _placeholder={{ color: 'inherit' }} disabled={disabled} />
      break
    case FormFieldVariants.PHONE:
      component = <Input type="phone" {...field} isInvalid={!!error} _placeholder={{ color: 'inherit' }} disabled={disabled} />
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
      component = <Input isInvalid={!!error} type="text" {...field} _placeholder={{ color: 'inherit' }} disabled={disabled} />
      break
    case FormFieldVariants.VOTE: {
      let values: VotingFieldOption[] = []
      try {
        values = JSON.parse(fieldProps.values)
      } catch (e) {
        console.error(e)
        values = []
      }
      component = (
        <VotingField onChange={field.onChange} value={field.value} options={values} required={fieldProps.required} disabled={disabled} />
      )
      break
    }
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
    case FormFieldVariants.SELECTION_GRID:
    case FormFieldVariants.CHOICE_GRID:
      component = (
        <GridField
          disabled={disabled}
          field={fieldProps}
          dirty={!!submittedValue}
          choice={fieldProps.type === FormFieldVariants.CHOICE_GRID}
        />
      )
      break
  }
  return (
    <>
      {component}
      {error && <Text color="red">{error.message}</Text>}
    </>
  )
}
