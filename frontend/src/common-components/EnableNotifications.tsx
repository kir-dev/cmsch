import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { areNotificationsSupported } from '@/util/configs/firebase.config.ts'
import { Info } from 'lucide-react'
import { useState } from 'react'

export const EnableNotifications = () => {
  const [isOpen, setIsOpen] = useState(true)
  const onClose = () => setIsOpen(false)

  const config = useConfigContext()
  if (!areNotificationsSupported()) return
  if (Notification.permission !== 'default') return null // we cannot ask again

  const component = config?.components?.pushnotification
  if (!component?.notificationsEnabled || !isOpen || !shouldShowAlert(component.permissionAllowNeverShowAgain)) return null

  const { permissionPromptText, permissionAcceptText, permissionDenyText } = component
  return (
    <div className="w-full">
      <Alert
        className={'mx-auto w-full max-w-full md:max-w-[64rem] mb-5 rounded-none md:rounded-xl bg-info text-info-foreground border-none'}
      >
        <div className="flex flex-row items-center justify-between w-full space-x-4">
          <Info className="hidden md:block h-5 w-5 shrink-0" />
          <div className="flex-1">
            <AlertDescription className="break-words">{permissionPromptText}</AlertDescription>
          </div>
          <div className="flex flex-row space-x-2 shrink-0">
            <Button
              variant="ghost"
              className="hover:bg-info-foreground/10"
              onClick={() => enableNotifications(onClose, () => window.location.reload())}
            >
              {permissionAcceptText || 'Igen'}
            </Button>
            {!!permissionDenyText && (
              <Button variant="ghost" className="hover:bg-info-foreground/10" onClick={() => disableNotifications(onClose)}>
                {permissionDenyText}
              </Button>
            )}
          </div>
        </div>
      </Alert>
    </div>
  )
}

function shouldShowAlert(allowedToNeverShowAgain: boolean | undefined) {
  return !allowedToNeverShowAgain || localStorage.getItem('show-notification-alert') !== 'false'
}

function disableAlert() {
  localStorage.setItem('show-notification-alert', 'false')
}

async function enableNotifications(onClose: () => void, onEnabled: () => void) {
  onClose()
  const result = await Notification.requestPermission()
  if (result === 'granted') {
    onEnabled()
  }
}

function disableNotifications(onClose: () => void) {
  onClose()
  disableAlert()
}
