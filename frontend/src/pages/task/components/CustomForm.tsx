import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { useToast } from '@/hooks/use-toast'
import { AbsolutePaths } from '@/util/paths'
import type { TaskFormatDescriptor } from '@/util/views/task.view'
import { useEffect } from 'react'
import { type Control, Controller, type FieldArrayWithId, type UseFieldArrayReplace } from 'react-hook-form'
import { useNavigate } from 'react-router'
import type { FormInput } from '../task.page'
import { InputWithAddon } from './InputWithAddon'

type CustomFormProps = {
  formatDescriptor: string | undefined
  control: Control<FormInput, object>
  fields: FieldArrayWithId<FormInput, 'customForm', 'id'>[]
  replace: UseFieldArrayReplace<FormInput, 'customForm'>
}

export const CustomForm = ({ formatDescriptor, control, fields, replace }: CustomFormProps) => {
  const { toast } = useToast()
  const navigate = useNavigate()

  useEffect(() => {
    if (formatDescriptor) {
      try {
        const inputFields: TaskFormatDescriptor[] = JSON.parse(formatDescriptor)
        replace(inputFields.map((field) => ({ value: '', ...field })))
      } catch (e) {
        console.error(e)
        toast({
          title: 'Érvénytelen feladat',
          description: 'A feladat űrlapjának formátuma érvénytelen.',
          variant: 'destructive'
        })
        navigate(AbsolutePaths.TASKS)
      }
    }
  }, [formatDescriptor, navigate, replace, toast])
  return (
    <>
      {fields.map((f, idx) => (
        <div className="mt-5 flex flex-col gap-2" key={f.id}>
          <Label htmlFor={`customForm.${idx}.value`}>{f.title}</Label>
          <Controller
            name={`customForm.${idx}.value`}
            control={control}
            render={({ field }) => (
              <InputWithAddon suffix={f.suffix}>
                {f.type === 'textarea' ? (
                  <Textarea id={`customForm.${idx}.value`} placeholder={f.title} {...field} />
                ) : (
                  <Input id={`customForm.${idx}.value`} placeholder={f.title} {...field} type={f.type} />
                )}
              </InputWithAddon>
            )}
          />
        </div>
      ))}
    </>
  )
}
