import { FormField, FormFieldVariants } from '../../../util/views/form.view'

interface AutoFormFieldProps {
  field: FormField
}

export const AutoFormField = ({ field }: AutoFormFieldProps) => {
  switch (field.type) {
    case FormFieldVariants.CHECKBOX:
    case FormFieldVariants.EMAIL:
    case FormFieldVariants.LONG_TEXT:
    case FormFieldVariants.MUST_AGREE:
    case FormFieldVariants.NUMBER:
    case FormFieldVariants.PHONE:
    case FormFieldVariants.SELECT:
    case FormFieldVariants.TEXT:
    default:
  }
  return <></>
}
