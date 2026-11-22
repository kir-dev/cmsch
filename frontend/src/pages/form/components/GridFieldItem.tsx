import { Checkbox } from '@/components/ui/checkbox'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'

type Props = {
  optionKey: string
  checked: boolean
  onCheck: (checked: boolean) => void
  disabled: boolean
  radio: boolean
}

export function GridFieldItem({ optionKey, checked, onCheck, disabled, radio }: Props) {
  if (radio) {
    return (
      <RadioGroup value={checked ? optionKey : ''} onValueChange={() => onCheck(true)} disabled={disabled}>
        <RadioGroupItem value={optionKey} className={checked ? 'border-primary text-primary' : ''} />
      </RadioGroup>
    )
  }

  return (
    <Checkbox
      checked={checked}
      onCheckedChange={(value) => onCheck(value === true)}
      disabled={disabled}
      className={checked ? 'border-primary bg-primary' : ''}
    />
  )
}
