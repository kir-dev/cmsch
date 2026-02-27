import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import type { Signup } from '@/api/contexts/config/types'
import { Badge } from '@/components/ui/badge'
import { cn } from '@/lib/utils'
import { FormStatus, FormStatusLangKeys } from '@/util/views/form.view'

interface FormStatusBadgeProps {
  status: FormStatus
}

export const FormStatusBadge = ({ status }: FormStatusBadgeProps) => {
  const config = useConfigContext()
  const component = config?.components?.form
  let variant: 'default' | 'secondary' | 'destructive' | 'outline' = 'secondary'
  let customClass = ''

  switch (status) {
    case FormStatus.ACCEPTED:
      customClass = 'bg-success text-success-foreground hover:bg-success/90'
      break
    case FormStatus.SUBMITTED:
      customClass = 'bg-warning text-warning-foreground hover:bg-warning/90'
      break
    case FormStatus.FULL:
    case FormStatus.REJECTED:
    case FormStatus.GROUP_NOT_PERMITTED:
      variant = 'destructive'
      break
  }

  return (
    <Badge className={cn('my-5', customClass)} variant={variant}>
      {component?.[FormStatusLangKeys[status] as keyof Signup] || 'Ismeretlen'}
    </Badge>
  )
}
