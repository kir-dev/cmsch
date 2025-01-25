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
  const { setValue, watch } = useFormContext()

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (radio) {
      setValue(fieldName, { ...watch(fieldName), [questionKey]: optionKey })
    } else {
      setValue(fieldName, { ...watch(fieldName), [`${questionKey}_${optionKey}`]: e.target.checked })
    }
  }

  return radio ? (
    <Radio isChecked={watch(fieldName)?.[questionKey] === optionKey} onChange={onChange} colorScheme="brand" isDisabled={disabled} />
  ) : (
    <Checkbox isChecked={watch(fieldName)?.[`${questionKey}_${optionKey}`]} onChange={onChange} colorScheme="brand" isDisabled={disabled} />
  )
}
