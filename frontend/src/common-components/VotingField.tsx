import { Button } from '@/components/ui/button'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { cn } from '@/lib/utils.ts'
import type { VotingFieldOption } from '@/util/views/form.view'

interface VotingFieldProps {
  onChange: (value?: string) => void
  value: string
  options: VotingFieldOption[]
  required: boolean
  disabled: boolean
}

export function VotingField({ options, onChange, value, required, disabled }: VotingFieldProps) {
  return (
    <div className="flex flex-col space-y-5 items-start w-full">
      <div className="flex justify-end w-full min-h-[40px]">
        {!required && !!value && !disabled && (
          <Button variant="outline" onClick={() => onChange('')}>
            Választásom törlése
          </Button>
        )}
      </div>
      <RadioGroup value={value} onValueChange={onChange} disabled={disabled} className="w-full space-y-4">
        {options
          .filter((item) => value === item.value || !disabled)
          .map((opt) => (
            <div key={opt.value} className="w-full">
              <VotingFieldElement onChange={() => onChange(opt.value)} selected={value === opt.value} option={opt} />
            </div>
          ))}
      </RadioGroup>
    </div>
  )
}

interface VotingFieldElementProps {
  onChange: () => void
  selected: boolean
  option: VotingFieldOption
}

function VotingFieldElement({ onChange, selected, option }: VotingFieldElementProps) {
  return (
    <div
      onClick={onChange}
      className={cn(
        'flex flex-col md:flex-row items-center cursor-pointer gap-5 rounded-md border p-4 relative w-full transition-colors',
        selected && 'bg-primary'
      )}
    >
      {option.img && <img className="rounded-md w-40 object-contain" src={option.img} alt={option.title} />}
      <div className="flex flex-col items-start">
        <h3 className="text-3xl font-bold">{option.title}</h3>
        <p>{option.text}</p>
      </div>
      <div className="absolute top-5 right-5">
        <RadioGroupItem value={option.value} id={option.value} className="h-6 w-6" />
      </div>
    </div>
  )
}
