import { Checkbox } from '@/components/ui/checkbox'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { useBrandColor } from '@/util/core-functions.util.ts'
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
  const brandColor = useBrandColor()

  if (radio) {
    const currentValue = watch(fieldName)?.[questionKey]
    return (
      <RadioGroup
        value={currentValue}
        onValueChange={(val) => setValue(fieldName, { ...watch(fieldName), [questionKey]: val })}
        disabled={disabled}
      >
        <RadioGroupItem value={optionKey} style={currentValue === optionKey ? { borderColor: brandColor, color: brandColor } : {}} />
      </RadioGroup>
    )
  }

  const isChecked = watch(fieldName)?.[`${questionKey}_${optionKey}`]
  return (
    <Checkbox
      checked={isChecked}
      onCheckedChange={(checked) => setValue(fieldName, { ...watch(fieldName), [`${questionKey}_${optionKey}`]: checked })}
      disabled={disabled}
      style={isChecked ? { borderColor: brandColor, backgroundColor: brandColor } : {}}
    />
  )
}
