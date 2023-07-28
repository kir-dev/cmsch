import { Checkbox, Grid, GridItem, Radio } from '@chakra-ui/react'
import { FormField, FormFieldVariants, GridFieldValues } from '../../../util/views/form.view'
import { Fragment, useEffect, useState } from 'react'

type Props = {
  field: FormField
  onChange: (value?: string) => void
  value: string
  variant: FormFieldVariants.CHOICE_GRID | FormFieldVariants.SELECTION_GRID
  disabled: boolean
}

export function GridField({ field, onChange, value, variant, disabled }: Props) {
  const format = JSON.parse(field.values) as GridFieldValues
  const [values, setValues] = useState<Record<string, string>>(JSON.parse(value || '{}'))
  const radio = variant === FormFieldVariants.CHOICE_GRID
  const getKey = (qKey: string, oKey: string) => `${format.prefix}${qKey}${radio ? '' : oKey}`

  useEffect(() => {
    if (Object.keys(values).length === 0) {
      const defaultValues: Record<string, string> = {}
      format.questions.forEach((q) => {
        format.options.forEach((o) => {
          defaultValues[getKey(q.key, o.key)] = radio ? format.options[0].key : 'false'
        })
      })
      setValues(defaultValues)
    }
  }, [])

  const onCheck = (qKey: string, oKey: string, checked?: boolean) => {
    const newValues = { ...values }
    newValues[getKey(qKey, oKey)] = radio ? oKey : checked?.toString() || 'false'
    setValues(newValues)
    onChange(JSON.stringify(newValues))
  }

  return (
    <Grid
      justifyItems="center"
      alignItems="center"
      templateRows={`repeat(${format.questions.length + 1}, 1fr)`}
      templateColumns={`repeat(${format.options.length + 1}, 1fr)`}
      gap={2}
    >
      <GridItem />
      {format.options.map((opt) => (
        <GridItem key={opt.key}>{opt.label}</GridItem>
      ))}

      {format.questions.map((q) => (
        <Fragment key={q.key}>
          <GridItem>{q.label}</GridItem>
          {format.options.map((o) => (
            <GridItem key={o.key}>
              {radio ? (
                <Radio
                  isDisabled={disabled}
                  onChange={(e) => onCheck(q.key, o.key)}
                  isChecked={values[getKey(q.key, o.key)] === o.key}
                  colorScheme="brand"
                />
              ) : (
                <Checkbox
                  onChange={(e) => onCheck(q.key, o.key, e.target.checked)}
                  isChecked={values[getKey(q.key, o.key)] === 'true'}
                  colorScheme="brand"
                  isDisabled={disabled}
                />
              )}
            </GridItem>
          ))}
        </Fragment>
      ))}
    </Grid>
  )
}
