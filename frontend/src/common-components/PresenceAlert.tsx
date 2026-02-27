import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { cn } from '@/lib/utils'
import { CheckCircle2, Info } from 'lucide-react'
import type { FC } from 'react'

type PresenceAlertProps = {
  acquired: number
  needed: number
  className?: string
  mt?: number
}

export const PresenceAlert: FC<PresenceAlertProps> = ({ acquired, needed, className }) => {
  const config = useConfigContext()
  const component = config?.components?.token
  const messageParts = component?.minTokenNotEnoughMessage?.split('{}') || ['']
  if (acquired == null || needed == null || !component?.collectFeatureEnabled) return null
  else if (acquired < needed)
    return (
      <Alert className={cn('border-l-4', className)}>
        <Info className="h-4 w-4" />
        <AlertDescription>
          {messageParts[0] + (messageParts.length > 1 ? (needed - acquired).toString() + messageParts[1] : '')}
        </AlertDescription>
      </Alert>
    )
  else
    return (
      <Alert className={cn('border-l-4 border-success', className)}>
        <CheckCircle2 className="h-4 w-4 text-success" />
        <AlertDescription>{component?.minTokenDoneMessage}</AlertDescription>
      </Alert>
    )
}
