import { useAuthContext } from '@/api/contexts/auth/useAuthContext.ts'
import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { ToastAction } from '@/components/ui/toast'
import { useToast } from '@/hooks/use-toast'
import { areNotificationsSupported, disableNotifications, getCloudMessaging, initNotifications } from '@/util/configs/firebase.config.ts'
import { ExternalLink } from 'lucide-react'
import { type FC, type PropsWithChildren, useEffect } from 'react'

export const PushNotificationHandler: FC<PropsWithChildren> = ({ children }) => {
  const authContext = useAuthContext()
  const config = useConfigContext()
  const { toast } = useToast()

  const hasPermission = areNotificationsSupported() && Notification.permission === 'granted'
  useEffect(() => {
    if (!hasPermission) return
    if (!authContext.isLoggedIn) return
    const shouldShowNotifications = config?.components?.pushnotification?.notificationsEnabled
    if (shouldShowNotifications) {
      const messaging = getCloudMessaging()
      if (messaging === null) return
      initNotifications(messaging, (payload) => {
        const { body, image, title } = payload.notification ?? {}
        const link = payload.fcmOptions?.link
        toast({
          title: title,
          description: (
            <div className="flex flex-col space-y-2 mt-2">
              {image && <img width={120} height={120} src={image} alt={title} className="rounded-md" />}
              {body && <p>{body}</p>}
            </div>
          ),
          action: link ? (
            <ToastAction altText="Megnyitás" asChild>
              <a href={link} target="_blank" rel="noreferrer" className="flex items-center">
                Megnyitás <ExternalLink className="ml-2 h-4 w-4" />
              </a>
            </ToastAction>
          ) : undefined
        })
      })
    } else {
      disableNotifications()
    }
  }, [authContext.isLoggedIn, config?.components?.pushnotification?.notificationsEnabled, hasPermission, toast])
  return <>{children}</>
}
