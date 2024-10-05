import { FormStatus, FormStatusLangKeys } from '../../../util/views/form.view'
import { Badge } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { Signup } from '../../../api/contexts/config/types'

interface FormStatusBadgeProps {
  status: FormStatus
}

export const FormStatusBadge = ({ status }: FormStatusBadgeProps) => {
  const config = useConfigContext()
  const component = config?.components.form
  let color = 'border'
  switch (status) {
    case FormStatus.ACCEPTED:
      color = 'success'
      break
    case FormStatus.SUBMITTED:
      color = 'warning'
      break
    case FormStatus.FULL:
      color = 'error'
      break
    case FormStatus.REJECTED:
      color = 'error'
      break
  }
  return (
    <Badge my={5} colorScheme={color}>
      {component?.[FormStatusLangKeys[status] as keyof Signup] || 'Ismeretlen'}
    </Badge>
  )
}
