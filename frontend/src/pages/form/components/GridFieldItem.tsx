import { Checkbox, Radio } from '@chakra-ui/react'
import type { ChangeEvent } from 'react'
import { useFormContext } from 'react-hook-form'
import { useBrandColor } from '../../../util/core-functions.util.ts'

type Props = {
  questionKey: string
  optionKey: string
  fieldName: string
  disabled: boolean
  radio: boolean
}

export function GridFieldItem({ questionKey, optionKey, fieldName, disabled, radio }: Props) {
  const { setValue, watch } = useFormContext()
  const brandColor = useBrandColor()

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (radio) {
      setValue(fieldName, { ...watch(fieldName), [questionKey]: optionKey })
    } else {
      setValue(fieldName, { ...watch(fieldName), [`${questionKey}_${optionKey}`]: e.target.checked })
    }
  }

  return radio ? (
    <Radio isChecked={watch(fieldName)?.[questionKey] === optionKey} onChange={onChange} colorScheme={brandColor} isDisabled={disabled} />
  ) : (
    <Checkbox
      isChecked={watch(fieldName)?.[`${questionKey}_${optionKey}`]}
      onChange={onChange}
      colorScheme={brandColor}
      isDisabled={disabled}
    />
  )
}
