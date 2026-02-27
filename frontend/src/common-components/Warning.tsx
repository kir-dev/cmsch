import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { cn } from '@/lib/utils'
import { AlertCircle, AlertTriangle, Info, X } from 'lucide-react'
import { useState } from 'react'

export const Warning = () => {
  const [isOpen, setIsOpen] = useState(true)
  const config = useConfigContext()
  const message = config?.components?.app?.warningMessage
  const level = config?.components?.app?.warningLevel

  if (!isOpen || !message) return null

  const getAlertStyles = () => {
    switch (level) {
      case 'error':
        return { bg: 'bg-danger', text: 'text-danger-foreground', icon: AlertCircle }
      case 'info':
        return { bg: 'bg-info', text: 'text-info-foreground', icon: Info }
      case 'warning':
      default:
        return { bg: 'bg-warning', text: 'text-warning-foreground', icon: AlertTriangle }
    }
  }

  const styles = getAlertStyles()
  const Icon = styles.icon

  return (
    <div className="w-full">
      <Alert
        className={cn('mx-auto w-full max-w-full md:max-w-[64rem] mb-5 rounded-none md:rounded-xl border-none', styles.bg, styles.text)}
      >
        <div className="flex items-center justify-between w-full space-x-4">
          <Icon className="h-5 w-5 shrink-0" />
          <div className="flex-1">
            <AlertDescription className="break-words">{message}</AlertDescription>
          </div>
          <button onClick={() => setIsOpen(false)} className="shrink-0 p-1 hover:bg-foreground/10 rounded-full transition-colors">
            <X className="h-4 w-4" />
          </button>
        </div>
      </Alert>
    </div>
  )
}
