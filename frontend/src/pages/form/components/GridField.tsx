import { PageStatus } from '@/common-components/PageStatus'
import type { FormField } from '@/util/views/form.view'
import { Fragment } from 'react'
import { GridFieldItem } from './GridFieldItem'
import { type GridValue, parseGridFormat } from './gridField.util'

type Props = {
  field: FormField
  choice: boolean
  disabled: boolean
  value: GridValue | undefined
  onChange: (value: GridValue) => void
}

export function GridField({ field, choice, disabled, value, onChange }: Props) {
  const format = parseGridFormat(field.values)

  if (!format) {
    console.error('Invalid format for the form field!')
    return <PageStatus isLoading={false} isError title={field.fieldName} />
  }

  const current: GridValue = value && typeof value === 'object' ? value : {}

  return (
    <div
      className="grid gap-2 items-center justify-items-center"
      style={{
        gridTemplateRows: `repeat(${format.questions.length + 1}, 1fr)`,
        gridTemplateColumns: `repeat(${format.options.length + 1}, 1fr)`
      }}
    >
      <div></div>
      {format.options.map((opt) => (
        <div key={opt.key}>{opt.label}</div>
      ))}

      {format.questions.map((q) => (
        <Fragment key={q.key}>
          <div>{q.label}</div>
          {format.options.map((o) => {
            const cellKey = choice ? q.key : `${q.key}_${o.key}`
            return (
              <div key={o.key}>
                <GridFieldItem
                  radio={choice}
                  disabled={disabled}
                  optionKey={o.key}
                  checked={choice ? current[cellKey] === o.key : current[cellKey] === true}
                  onCheck={(checked) => onChange({ ...current, [cellKey]: choice ? o.key : checked })}
                />
              </div>
            )
          })}
        </Fragment>
      ))}
    </div>
  )
}
