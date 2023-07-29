import { Grid, GridItem } from '@chakra-ui/react'
import { FormField, GridFieldValues } from '../../../util/views/form.view'
import { Fragment, useEffect } from 'react'
import { GridFieldItem } from './GridFieldItem'
import { useFormContext } from 'react-hook-form'

type Props = {
  field: FormField
  choice: boolean
  disabled: boolean
}

export function GridField({ field, choice, disabled }: Props) {
  const format = JSON.parse(field.values) as GridFieldValues
  const { setValue } = useFormContext()

  useEffect(() => {
    if (choice) {
      format.questions.forEach((q) => {
        setValue(`${field.fieldName}${q.key}`, format.options[0].key)
      })
    }
  }, [])
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
              <GridFieldItem radio={choice} disabled={disabled} questionKey={q.key} optionKey={o.key} fieldName={field.fieldName} />
            </GridItem>
          ))}
        </Fragment>
      ))}
    </Grid>
  )
}
