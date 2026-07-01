import { Checkbox } from '@/components/ui/checkbox'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
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

  if (radio) {
    const currentValue = watch(fieldName)?.[questionKey]
    return (
      <RadioGroup
        value={currentValue}
        onValueChange={(val) => setValue(fieldName, { ...watch(fieldName), [questionKey]: val })}
        disabled={disabled}
      >
        <RadioGroupItem value={optionKey} className={currentValue === optionKey ? 'border-primary text-primary' : ''} />
      </RadioGroup>
    )
  }

  const isChecked = watch(fieldName)?.[`${questionKey}_${optionKey}`]
  return (
    <Checkbox
      checked={isChecked}
      onCheckedChange={(checked) => setValue(fieldName, { ...watch(fieldName), [`${questionKey}_${optionKey}`]: checked })}
      disabled={disabled}
      className={isChecked ? 'border-primary bg-primary' : ''}
    />
  )
}
