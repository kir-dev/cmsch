import { Grid, GridItem, Input } from '@chakra-ui/react'
import { FormField, GridFieldValues } from '../../../util/views/form.view'
import { Fragment, useEffect } from 'react'
import { GridFieldItem } from './GridFieldItem'
import { useFormContext } from 'react-hook-form'
import { PageStatus } from '../../../common-components/PageStatus'

type Props = {
  field: FormField
  choice: boolean
  disabled: boolean
  dirty: boolean
}

export function GridField({ field, choice, disabled, dirty }: Props) {
  try {
    const format: GridFieldValues = JSON.parse(field.values)
    if (!isValidGridField(format)) {
      throw new Error('Invalid form format')
    }
    const { setValue, watch, register } = useFormContext()

    useEffect(() => {
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
    }, [])
    return (
      <Grid
        justifyItems="center"
        alignItems="center"
        templateRows={`repeat(${format.questions.length + 1}, 1fr)`}
        templateColumns={`repeat(${format.options.length + 1}, 1fr)`}
        gap={2}
      >
        <Input {...register(field.fieldName)} disabled hidden />
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
  } catch {
    console.error('Invalid format for the form field!')
    return <PageStatus isLoading={false} isError title={field.fieldName} />
  }
}

function isValidGridField(metadata: any): metadata is GridFieldValues {
  return (
    Array.isArray(metadata.questions) &&
    Array.isArray(metadata.options) &&
    ![...metadata.questions, ...metadata.options].some((v) => !v.label || !v.key)
  )
}
