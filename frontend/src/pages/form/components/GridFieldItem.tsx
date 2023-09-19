import { Checkbox, Radio } from '@chakra-ui/react'
import { ChangeEvent } from 'react'
import { useFormContext } from 'react-hook-form'

type Props = {
  questionKey: string
  optionKey: string
  fieldName: string
  disabled: boolean
  radio: boolean
}

export function GridFieldItem({ questionKey, optionKey, fieldName, disabled, radio }: Props) {
  const { register, setValue, watch } = useFormContext()

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (radio) {
      setValue(`${fieldName}${questionKey}`, optionKey)
    } else {
      setValue(`${fieldName}${questionKey}${optionKey}`, `${e.target.checked}`)
    }
  }

  return radio ? (
    <Radio
      {...register(`${fieldName}${questionKey}`)}
      isChecked={watch(`${fieldName}${questionKey}`) === optionKey}
      onChange={onChange}
      colorScheme="brand"
      isDisabled={disabled}
    />
  ) : (
    <Checkbox
      {...register(`${fieldName}${questionKey}${optionKey}`)}
      isChecked={`${watch(`${fieldName}${questionKey}${optionKey}`)}` === 'true'}
      onChange={onChange}
      colorScheme="brand"
      isDisabled={disabled}
    />
  )
}
