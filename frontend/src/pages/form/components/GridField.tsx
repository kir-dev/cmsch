import { Grid, GridItem } from '@chakra-ui/react'
import { FormField, GridFieldValues } from '../../../util/views/form.view'
import { Fragment, useEffect } from 'react'
import { GridFieldItem } from './GridFieldItem'
import { useFormContext } from 'react-hook-form'
import { PageStatus } from '../../../common-components/PageStatus'

type Props = {
  field: FormField
  choice: boolean
  disabled: boolean
}

export function GridField({ field, choice, disabled }: Props) {
  try {
    const format: GridFieldValues = JSON.parse(field.values)
    if (!isValidGridField(format)) {
      throw new Error('Invalid form format')
    }
    const { setValue } = useFormContext()

    useEffect(() => {
      if (choice) {
        format.questions.forEach((q) => {
          setValue(`${format.prefix}${field.fieldName}${q.key}`, format.options[0].key)
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
                <GridFieldItem
                  radio={choice}
                  disabled={disabled}
                  questionKey={q.key}
                  optionKey={o.key}
                  fieldName={format.prefix + field.fieldName}
                />
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
    typeof metadata.prefix === 'string' &&
    Array.isArray(metadata.questions) &&
    Array.isArray(metadata.options) &&
    ![...metadata.questions, ...metadata.options].some((v) => !v.label || !v.key)
  )
}
