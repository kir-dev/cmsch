import { PageStatus } from '@/common-components/PageStatus'
import type { FormField, GridFieldValues } from '@/util/views/form.view'
import { Fragment, useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
import { GridFieldItem } from './GridFieldItem'

type Props = {
  field: FormField
  choice: boolean
  disabled: boolean
  dirty: boolean
}

export function GridField({ field, choice, disabled, dirty }: Props) {
  const format: GridFieldValues = JSON.parse(field.values)
  const isFormatValid = isValidGridField(format)

  const { setValue, watch, register } = useFormContext()

  useEffect(() => {
    if (!isFormatValid) {
      console.error('Invalid format for the form field!')
      return
    }
    if (!dirty) {
      if (choice) {
        format.questions.forEach((q) => {
          setValue(field.fieldName, { ...watch(field.fieldName), [q.key]: format.options[0].key })
        })
      } else {
        format.questions.forEach((q) => {
          format.options.forEach((o) => {
            setValue(field.fieldName, { ...watch(field.fieldName), [`${q.key}_${o.key}`]: false })
          })
        })
      }
    }
  }, [choice, dirty, field.fieldName, format.options, format.questions, isFormatValid, setValue, watch])

  if (!isFormatValid) {
    return <PageStatus isLoading={false} isError title={field.fieldName} />
  }

  return (
    <div
      className="grid gap-2 items-center justify-items-center"
      style={{
        gridTemplateRows: `repeat(${format.questions.length + 1}, 1fr)`,
        gridTemplateColumns: `repeat(${format.options.length + 1}, 1fr)`
      }}
    >
      <input {...register(field.fieldName)} disabled hidden />
      <div></div>
      {format.options.map((opt) => (
        <div key={opt.key}>{opt.label}</div>
      ))}

      {format.questions.map((q) => (
        <Fragment key={q.key}>
          <div>{q.label}</div>
          {format.options.map((o) => (
            <div key={o.key}>
              <GridFieldItem radio={choice} disabled={disabled} questionKey={q.key} optionKey={o.key} fieldName={field.fieldName} />
            </div>
          ))}
        </Fragment>
      ))}
    </div>
  )
}

function isValidGridField(metadata: GridFieldValues): metadata is GridFieldValues {
  return (
    Array.isArray(metadata.questions) &&
    Array.isArray(metadata.options) &&
    ![...metadata.questions, ...metadata.options].some((v) => !v.label || !v.key)
  )
}
