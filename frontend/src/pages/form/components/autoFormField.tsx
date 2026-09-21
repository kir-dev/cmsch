import Markdown from '@/common-components/Markdown'
import { VotingField } from '@/common-components/VotingField'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Checkbox } from '@/components/ui/checkbox'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Textarea } from '@/components/ui/textarea'
import { isCheckbox, isGridField, isMultiCheckbox } from '@/util/core-functions.util'
import { type FormField, FormFieldVariants, type VotingFieldOption } from '@/util/views/form.view'
import { AlertTriangle, Info } from 'lucide-react'
import { type ReactNode } from 'react'
import { type Control, useController } from 'react-hook-form'
import { GridField } from './GridField'

interface AutoFormFieldProps {
  fieldProps: FormField
  control: Control
  disabled: boolean
  submittedValue?: string
}

const parseMultiCheckboxValue = (value: string | undefined): string[] => {
  if (!value) return []
  try {
    const parsed: unknown = JSON.parse(value)
    return Array.isArray(parsed) && parsed.every((item): item is string => typeof item === 'string') ? parsed : []
  } catch {
    return value
      .split(',')
      .map((item) => item.trim())
      .filter(Boolean)
  }
}

const parseGridValue = (value: string): unknown => {
  try {
    return JSON.parse(value)
  } catch (e) {
    console.error(e)
    return {}
  }
}

export const AutoFormField = ({ fieldProps, control, disabled, submittedValue }: AutoFormFieldProps) => {
  const selectValues = fieldProps.values
    .split(',')
    .map((opt) => opt.trim())
    .filter(Boolean)
  let defaultValue: unknown = isCheckbox(fieldProps.type) ? fieldProps.defaultValue === 'true' : fieldProps.defaultValue
  let requiredValue = fieldProps.required

  if (submittedValue) {
    if (isCheckbox(fieldProps.type)) defaultValue = submittedValue === 'true'
    else if (isMultiCheckbox(fieldProps.type)) defaultValue = parseMultiCheckboxValue(submittedValue)
    else if (isGridField(fieldProps.type)) defaultValue = parseGridValue(submittedValue)
    else defaultValue = submittedValue
  } else if (isMultiCheckbox(fieldProps.type)) {
    defaultValue = parseMultiCheckboxValue(fieldProps.defaultValue)
  } else if (!defaultValue) {
    if (fieldProps.type === FormFieldVariants.SELECT) defaultValue = selectValues[0]
    else defaultValue = ''
  }

  if (fieldProps.type.startsWith('INJECT_')) {
    requiredValue = false
  }

  const formatPattern = new RegExp(fieldProps.formatRegex)
  const formatMessage = 'A formátum nem megfelelő!'
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
      pattern: isMultiCheckbox(fieldProps.type) ? undefined : { value: formatPattern, message: formatMessage },
      validate: isMultiCheckbox(fieldProps.type)
        ? (value: unknown) => !Array.isArray(value) || value.every((item) => formatPattern.test(item)) || formatMessage
        : undefined
    }
  })
  let component: ReactNode = null
  switch (fieldProps.type) {
    case FormFieldVariants.CHECKBOX:
      component = (
        <div className="flex items-center mt-10 space-x-3">
          <Checkbox id={fieldProps.fieldName} checked={field.value} onCheckedChange={field.onChange} disabled={disabled} />
          <Label htmlFor={fieldProps.fieldName} className="text-xl font-normal cursor-pointer">
            {fieldProps.label}
          </Label>
        </div>
      )
      break
    case FormFieldVariants.MULTI_CHECKBOX: {
      const selected = Array.isArray(field.value) ? field.value : []
      component = (
        <fieldset className="space-y-3">
          <legend className="sr-only">{fieldProps.label}</legend>
          {selectValues.map((option, index) => {
            const id = `${fieldProps.fieldName}-${index}`
            return (
              <div key={id} className="flex items-center space-x-3">
                <Checkbox
                  id={id}
                  checked={selected.includes(option)}
                  onCheckedChange={(value) => {
                    field.onChange(value ? [...selected, option] : selected.filter((item: string) => item !== option))
                  }}
                  disabled={disabled}
                />
                <Label htmlFor={id} className="font-normal cursor-pointer">
                  {option}
                </Label>
              </div>
            )
          })}
        </fieldset>
      )
      break
    }
    case FormFieldVariants.EMAIL:
      component = <Input type="email" {...field} disabled={disabled} className={error ? 'border-destructive' : ''} />
      break
    case FormFieldVariants.LONG_TEXT:
      component = <Textarea {...field} disabled={disabled} className={error ? 'border-destructive' : ''} />
      break
    case FormFieldVariants.MUST_AGREE:
      component = (
        <div className="flex items-center my-10 space-x-3">
          <Checkbox id={fieldProps.fieldName} checked={field.value} onCheckedChange={field.onChange} disabled={disabled} />
          <Label htmlFor={fieldProps.fieldName} className="text-xl font-normal cursor-pointer">
            {fieldProps.label}
          </Label>
        </div>
      )
      break
    case FormFieldVariants.DATE:
      component = <Input type="date" {...field} disabled={disabled} className={error ? 'border-destructive' : ''} />
      break
    case FormFieldVariants.NUMBER:
      component = <Input type="number" {...field} disabled={disabled} className={error ? 'border-destructive' : ''} />
      break
    case FormFieldVariants.PHONE:
      component = <Input type="tel" {...field} disabled={disabled} className={error ? 'border-destructive' : ''} />
      break
    case FormFieldVariants.SELECT:
      component = (
        <Select onValueChange={field.onChange} value={field.value} disabled={disabled}>
          <SelectTrigger>
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {selectValues.map((opt) => (
              <SelectItem key={opt} value={opt}>
                {opt}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      )
      break
    case FormFieldVariants.TEXT:
      component = <Input type="text" {...field} disabled={disabled} className={error ? 'border-destructive' : ''} />
      break
    case FormFieldVariants.VOTE: {
      let values: VotingFieldOption[]
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
        <Alert className="bg-info/10 border-l-4 border-l-info">
          <Info className="h-4 w-4" />
          <AlertDescription>
            <Markdown text={fieldProps.values} />
          </AlertDescription>
        </Alert>
      )
      break
    case FormFieldVariants.WARNING_BOX:
      component = (
        <Alert className="bg-warning/10 border-l-4 border-l-warning">
          <AlertTriangle className="h-4 w-4" />
          <AlertDescription>
            <Markdown text={fieldProps.values} />
          </AlertDescription>
        </Alert>
      )
      break
    case FormFieldVariants.TEXT_BOX:
      component = <Markdown text={fieldProps.values} />
      break
    case FormFieldVariants.SECTION_START:
      component = <span className="text-3xl font-bold">{fieldProps.values}</span>
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
      {error && <p className="text-destructive mt-1">{error.message}</p>}
    </>
  )
}
