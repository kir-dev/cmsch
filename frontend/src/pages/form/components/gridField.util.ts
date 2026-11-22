import type { FormField, GridFieldValues } from '@/util/views/form.view'

export type GridValue = Record<string, string | boolean>

export function parseGridFormat(values: string): GridFieldValues | undefined {
  try {
    const parsed: GridFieldValues = JSON.parse(values)
    return parsed &&
      Array.isArray(parsed.questions) &&
      Array.isArray(parsed.options) &&
      ![...parsed.questions, ...parsed.options].some((v) => !v?.label || !v?.key)
      ? parsed
      : undefined
  } catch {
    return undefined
  }
}

export function getDefaultGridValue(field: FormField, choice: boolean): GridValue {
  const format = parseGridFormat(field.values)
  const value: GridValue = {}
  format?.questions.forEach((q) => {
    if (choice) {
      if (format.options[0]) value[q.key] = format.options[0].key
    } else {
      format.options.forEach((o) => {
        value[`${q.key}_${o.key}`] = false
      })
    }
  })
  return value
}
