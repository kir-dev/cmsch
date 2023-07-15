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
  let color = 'gray'
  switch (status) {
    case FormStatus.ACCEPTED:
      color = 'green'
      break
    case FormStatus.SUBMITTED:
      color = 'yellow'
      break
    case FormStatus.FULL:
      color = 'red'
      break
    case FormStatus.REJECTED:
      color = 'red'
      break
  }
  return (
    <Badge my={5} colorScheme={color}>
      {component?.[FormStatusLangKeys[status] as keyof Signup] || 'Ismeretlen'}
    </Badge>
  )
}
